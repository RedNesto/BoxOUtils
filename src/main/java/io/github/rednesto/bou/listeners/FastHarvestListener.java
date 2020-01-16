/*
 * MIT License
 *
 * Copyright (c) 2019 RedNesto
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.rednesto.bou.listeners;

import io.github.rednesto.bou.Config;
import io.github.rednesto.bou.CropsAlgoritm;
import io.github.rednesto.bou.CustomDropsProcessor;
import io.github.rednesto.bou.api.BouEventContextKeys;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.fastharvest.FastHarvestCrop;
import io.github.rednesto.bou.api.fastharvest.FastHarvestTools;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

import javax.annotation.Nullable;

import static io.github.rednesto.bou.Config.FastHarvest;

public class FastHarvestListener {

    private static final Map<String, CropDefinition> DEFINITIONS = new HashMap<>();

    static {
        DEFINITIONS.put("minecraft:wheat", new CropDefinition(7, ItemTypes.WHEAT_SEEDS, 3, ItemTypes.WHEAT, 0));
        DEFINITIONS.put("minecraft:carrots", new CropDefinition(7, ItemTypes.CARROT, 3, null, 0));
        DEFINITIONS.put("minecraft:potatoes", new CropDefinition(7, ItemTypes.POTATO, 3, null, 0));
        DEFINITIONS.put("minecraft:beetroots", new CropDefinition(3, ItemTypes.BEETROOT_SEEDS, 3, ItemTypes.BEETROOT, 0));
        DEFINITIONS.put("minecraft:cocoa", new CropDefinition(2, ItemTypes.DYE, 3, null, 0));
        DEFINITIONS.put("minecraft:nether_wart", new CropDefinition(3, ItemTypes.NETHER_WART, 2, null, 0));
    }

    @Listener
    public void onSecondaryClick(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        FastHarvest fastHarvest = Config.getFastHarvest();
        if (!fastHarvest.enabled) {
            return;
        }

        BlockSnapshot targetBlock = event.getTargetBlock();
        CropDefinition cropDefinition = DEFINITIONS.get(targetBlock.getState().getType().getId());
        if (cropDefinition == null) {
            return;
        }

        ItemStack itemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());
        int age = targetBlock.get(Keys.GROWTH_STAGE).orElse(0);
        int maxAge = cropDefinition.maxAge;
        if (!Config.canHarvest(itemInHand.getType().getId()) || age != maxAge) {
            return;
        }

        Location<World> entitiesSpawnLocation = targetBlock.getLocation().orElse(player.getLocation());

        Map<String, List<CustomLoot>> drops = Config.getBlocksDrops().drops;
        List<CustomLoot> customLoots = drops.get(targetBlock.getState().getType().getId());
        CustomLootProcessingContext processingContext = null;
        List<CustomLoot> lootsToUse = Collections.emptyList();
        if (customLoots != null) {
            lootsToUse = CustomDropsProcessor.getLootsToUse(customLoots, targetBlock, event.getCause());
            if (!lootsToUse.isEmpty()) {
                processingContext = new CustomLootProcessingContext(lootsToUse, event, targetBlock, event.getCause(), player, entitiesSpawnLocation);
                CustomDropsProcessor.dropLoot(processingContext, fastHarvest.dropInWorld);
            }
        }

        try (CauseStackManager.StackFrame stackFrame = Sponge.getCauseStackManager().pushCauseFrame()) {
            if (processingContext != null) {
                stackFrame.addContext(BouEventContextKeys.CUSTOM_LOOT_PROCESSING_CONTEXT, processingContext);
            }
            stackFrame.addContext(BouEventContextKeys.IS_FAST_HARVESTING, true);

            stackFrame.pushCause(targetBlock);

            Config.CropsControl cropsControl = Config.getCropsControl();

            boolean overwrite = false;
            if (processingContext != null) {
                List<CustomLoot> loots = processingContext.getLoots();
                for (CustomLoot loot : loots) {
                    if (loot.isOverwrite()) {
                        overwrite = true;
                        break;
                    }
                }
            }

            if (!overwrite) {
                FastHarvestCrop seedConfig;
                FastHarvestCrop productConfig = null;
                ItemType productType = cropDefinition.product;
                if (cropsControl.enabled) {
                    seedConfig = cropsControl.crops.get(cropDefinition.seed.getId());
                    if (productType != null) {
                        productConfig = cropsControl.crops.get(productType.getId());
                    }
                } else {

                    if (cropDefinition.seed == ItemTypes.DYE) {
                        seedConfig = new FastHarvestCrop(-1, -1, 0, 1, 3);
                    } else {
                        seedConfig = FastHarvestCrop.createDefault(cropDefinition.seedCount);
                    }

                    if (productType != null) {
                        productConfig = FastHarvestCrop.createDefault(cropDefinition.productCount);
                    }
                }

                int fortuneLevel = event.getContext().get(EventContextKeys.USED_ITEM)
                        .flatMap(item -> item.get(Keys.ITEM_ENCHANTMENTS))
                        .flatMap(enchantments -> enchantments.stream()
                                .filter(enchantment -> enchantment.getType().equals(EnchantmentTypes.FORTUNE))
                                .map(Enchantment::getLevel)
                                .findFirst())
                        .orElse(0);

                if (seedConfig != null) {
                    // We decrement because we consume the seed to plant it again
                    process(age, maxAge, fortuneLevel, seedConfig, cropDefinition.seed, true,
                            player, fastHarvest.dropInWorld, entitiesSpawnLocation, lootsToUse);
                }

                if (productType != null && productConfig != null) {
                    process(age, maxAge, fortuneLevel, productConfig, productType, false,
                            player, fastHarvest.dropInWorld, entitiesSpawnLocation, lootsToUse);
                }
            }
        }

        targetBlock.getLocation().ifPresent(location -> {
            BlockState oldState = targetBlock.getState();
            BlockState newState = oldState.with(Keys.GROWTH_STAGE, 0).orElse(oldState);
            location.setBlock(newState);
        });

        FastHarvestTools fhTools = fastHarvest.tools;
        if (fhTools.isDamageOnUse()) {
            itemInHand.get(DurabilityData.class).map(DurabilityData::durability).ifPresent(durability -> {
                durability.set(durability.get() - 1);
                itemInHand.offer(durability);
            });
        }
    }

    private static void process(int age, int maxAge, int fortuneLevel, FastHarvestCrop cropConfig,
                                ItemType itemType, boolean decrementQuantity, Player player, boolean spawnInWorld,
                                Location<World> spawnLocation, List<CustomLoot> customLoots) {
        ItemStack itemStack = createItemStack(age, maxAge, fortuneLevel, cropConfig, itemType, decrementQuantity);
        if (itemStack == null) {
            return;
        }

        if (spawnInWorld) {
            spawnItem(spawnLocation, itemStack);
        } else {
            if (!customLoots.isEmpty()) {
                for (CustomLoot loot : customLoots) {
                    CustomLoot.Reuse lootReuse = loot.getReuse();
                    if (lootReuse != null) {
                        List<ItemStack> reuseResult = new ArrayList<>();
                        CustomDropsProcessor.computeSingleItemReuse(lootReuse, reuseResult, itemStack);
                        if (!reuseResult.isEmpty()) {
                            if (reuseResult.size() == 1) {
                                player.getInventory().offer(reuseResult.get(0));
                            } else {
                                reuseResult.forEach(player.getInventory()::offer);
                            }

                            reuseResult.clear();
                        }
                    }
                }
            }

            player.getInventory().offer(itemStack);
        }
    }

    @Listener
    public void onFastHarvestCropsSpawn(SpawnEntityEvent.Custom event, @First BlockSnapshot blockSnapshot) {
        boolean isFastHarvesting = event.getContext().get(BouEventContextKeys.IS_FAST_HARVESTING).orElse(false);
        if (!isFastHarvesting) {
            return;
        }

        CustomLootProcessingContext processingContext = event.getContext().get(BouEventContextKeys.CUSTOM_LOOT_PROCESSING_CONTEXT).orElse(null);
        if (processingContext == null) {
            return;
        }

        for (CustomLoot loot : processingContext.getLoots()) {
            CustomDropsProcessor.handleDropItemEvent(event, loot, blockSnapshot);
        }
    }

    @Nullable
    private static ItemStack createItemStack(int age, int maxAge, int fortuneLevel, FastHarvestCrop cropConfig,
                                             ItemType itemType, boolean decrementQuantity) {
        CropsAlgoritm algorithm = CropsAlgoritm.ALG_19;
        if (itemType == ItemTypes.NETHER_WART) {
            algorithm = CropsAlgoritm.NETHER_WART;
        }

        int quantity = algorithm.compute(cropConfig, age, maxAge, fortuneLevel);
        if (decrementQuantity) {
            quantity--;
        }

        if (quantity > 0) {
            final ItemStack stack = ItemStack.of(itemType, quantity);
            if (itemType == ItemTypes.DYE) {
                stack.offer(Keys.DYE_COLOR, DyeColors.BROWN);
            }

            return stack;
        }

        return null;
    }

    private static void spawnItem(Location<World> spawnLocation, ItemStack itemStack) {
        ItemStackSnapshot representedItem = itemStack.createSnapshot();
        Entity entity = spawnLocation.createEntity(EntityTypes.ITEM);
        entity.offer(Keys.REPRESENTED_ITEM, representedItem);
        spawnLocation.spawnEntity(entity);
    }

    private static class CropDefinition {

        private final int maxAge;
        private final ItemType seed;
        private final int seedCount;
        @Nullable
        private final ItemType product;
        private final int productCount;

        private CropDefinition(int maxAge, ItemType seed, int seedCount, @Nullable ItemType product, int productCount) {
            this.maxAge = maxAge;
            this.seed = seed;
            this.seedCount = seedCount;
            this.product = product;
            this.productCount = productCount;
        }
    }
}
