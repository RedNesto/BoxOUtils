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

import io.github.rednesto.bou.*;
import io.github.rednesto.bou.api.BouEventContextKeys;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.fastharvest.FastHarvestCrop;
import io.github.rednesto.bou.api.fastharvest.FastHarvestTools;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.ContextValue;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.*;

import static io.github.rednesto.bou.Config.FastHarvest;

public class FastHarvestListener implements SpongeConfig.ReloadableListener {

    private static final Map<String, CropDefinition> DEFINITIONS = new HashMap<>();

    static {
        DEFINITIONS.put("minecraft:wheat", new CropDefinition(7, ItemTypes.WHEAT_SEEDS, 3, ItemTypes.WHEAT, 0));
        DEFINITIONS.put("minecraft:carrots", new CropDefinition(7, ItemTypes.CARROT, 3, null, 0));
        DEFINITIONS.put("minecraft:potatoes", new CropDefinition(7, ItemTypes.POTATO, 3, null, 0));
        DEFINITIONS.put("minecraft:beetroots", new CropDefinition(3, ItemTypes.BEETROOT_SEEDS, 3, ItemTypes.BEETROOT, 0));
        DEFINITIONS.put("minecraft:cocoa", new CropDefinition(2, ItemTypes.COCOA_BEANS, 3, null, 0));
        DEFINITIONS.put("minecraft:nether_wart", new CropDefinition(3, ItemTypes.NETHER_WART, 2, null, 0));
    }

    private final IdSelector.Cache blocksDropIdsMappingCache = new IdSelector.Cache();
    private final IdSelector.Cache seedIdsMappingCache = new IdSelector.Cache();
    private final IdSelector.Cache productIdsMappingCache = new IdSelector.Cache();

    @Listener
    public void onSecondaryClick(InteractBlockEvent.Secondary event, @First ServerPlayer player, @ContextValue("USED_HAND") HandType usedHand) {
        FastHarvest fastHarvest = Config.getFastHarvest();
        if (!fastHarvest.enabled || usedHand != HandTypes.MAIN_HAND.get()) { // FIXME Sponge is sending the Secondary event for both hands (╯°□°）╯︵ ┻━┻
            return;
        }

        BlockSnapshot targetBlock = event.block();
        ResourceKey blockId = RegistryTypes.BLOCK_TYPE.keyFor(Sponge.game(), targetBlock.state().type());
        CropDefinition cropDefinition = DEFINITIONS.get(blockId.formatted());
        if (cropDefinition == null) {
            return;
        }

        ItemStackSnapshot usedItem = event.context().get(EventContextKeys.USED_ITEM).orElseGet(ItemStackSnapshot::empty);
        int age = targetBlock.get(Keys.GROWTH_STAGE).orElse(0);
        int maxAge = cropDefinition.maxAge;
        ResourceKey usedItemId = RegistryTypes.ITEM_TYPE.keyFor(Sponge.game(), usedItem.type());
        if (!Config.canHarvest(usedItemId.formatted()) || age != maxAge) {
            return;
        }

        ServerLocation entitiesSpawnLocation = SpongeUtils.getCenteredLocation(targetBlock, player.world());

        Map<String, List<CustomLoot>> drops = Config.getBlocksDrops().drops;
        @Nullable List<CustomLoot> customLoots = blocksDropIdsMappingCache.get(drops, blockId.formatted());
        List<CustomLoot> lootsToUse = Collections.emptyList();
        CustomLootProcessingContext processingContext = new CustomLootProcessingContext(lootsToUse, event, targetBlock, event.cause(), player, entitiesSpawnLocation);
        if (customLoots != null) {
            lootsToUse = CustomDropsProcessor.getLootsToUse(customLoots, processingContext);
        }

        if (!lootsToUse.isEmpty()) {
            processingContext = processingContext.withLoots(lootsToUse);
            CustomDropsProcessor.dropLoot(processingContext);
        }

        try (CauseStackManager.StackFrame stackFrame = Sponge.server().causeStackManager().pushCauseFrame()) {
            if (!processingContext.getLoots().isEmpty()) {
                stackFrame.addContext(BouEventContextKeys.CUSTOM_LOOT_PROCESSING_CONTEXT, processingContext);
            }
            stackFrame.addContext(BouEventContextKeys.IS_FAST_HARVESTING, true);

            stackFrame.pushCause(targetBlock);

            Config.CropsControl cropsControl = Config.getCropsControl();

            boolean overwrite = false;
            List<CustomLoot> loots = processingContext.getLoots();
            for (CustomLoot loot : loots) {
                if (loot.isOverwrite()) {
                    overwrite = true;
                    break;
                }
            }

            if (!overwrite) {
                @Nullable FastHarvestCrop seedConfig;
                @Nullable FastHarvestCrop productConfig = null;
                @Nullable DefaultedRegistryReference<ItemType> productType = cropDefinition.product;
                if (cropsControl.enabled) {
                    seedConfig = seedIdsMappingCache.get(cropsControl.crops, cropDefinition.seed.location().formatted());
                    if (productType != null) {
                        productConfig = productIdsMappingCache.get(cropsControl.crops, productType.location().formatted());
                    }
                } else {

                    if (cropDefinition.seed.get().isAnyOf(ItemTypes.COCOA_BEANS.get())) {
                        seedConfig = new FastHarvestCrop(-1, -1, 0, 1, 3);
                    } else {
                        seedConfig = FastHarvestCrop.createDefault(cropDefinition.seedCount);
                    }

                    if (productType != null) {
                        productConfig = FastHarvestCrop.createDefault(cropDefinition.productCount);
                    }
                }

                int fortuneLevel = event.context().get(EventContextKeys.USED_ITEM)
                        .flatMap(item -> item.get(Keys.APPLIED_ENCHANTMENTS))
                        .flatMap(enchantments -> enchantments.stream()
                                .filter(enchantment -> enchantment.type().equals(EnchantmentTypes.FORTUNE.get()))
                                .map(Enchantment::level)
                                .findFirst())
                        .orElse(0);

                if (seedConfig != null) {
                    // We decrement because we consume the seed to plant it again
                    process(age, maxAge, fortuneLevel, seedConfig, cropDefinition.seed, true, fastHarvest.dropInWorld, processingContext);
                }

                if (productType != null && productConfig != null) {
                    process(age, maxAge, fortuneLevel, productConfig, productType, false, fastHarvest.dropInWorld, processingContext);
                }
            }
        }

        targetBlock.location().ifPresent(location -> {
            BlockState oldState = targetBlock.state();
            BlockState newState = oldState.with(Keys.GROWTH_STAGE, 0).orElse(oldState);
            location.setBlock(newState);
        });

        FastHarvestTools fhTools = fastHarvest.tools;
        if (fhTools.isDamageOnUse()) {
            player.itemInHand(usedHand).transform(Keys.ITEM_DURABILITY, durability -> durability - 1);
        }
    }

    private static void process(int age, int maxAge, int fortuneLevel, FastHarvestCrop cropConfig,
                                DefaultedRegistryReference<ItemType> itemType, boolean decrementQuantity, boolean dropInWorld,
                                CustomLootProcessingContext context) {
        @Nullable ItemStack itemStack = createItemStack(age, maxAge, fortuneLevel, cropConfig, itemType, decrementQuantity);
        if (itemStack == null) {
            return;
        }

        if (context.getLoots().isEmpty()) {
            if (dropInWorld && context.getTargetLocation() != null) {
                spawnItem(context.getTargetLocation(), itemStack);
            } else if (context.getTargetPlayer() != null) {
                context.getTargetPlayer().inventory().offer(itemStack);
            }
            return;
        }

        try (CauseStackManager.StackFrame reuseFrame = Sponge.server().causeStackManager().pushCauseFrame()) {
            reuseFrame.addContext(BouEventContextKeys.IS_REUSE_DROPS, true);

            for (CustomLoot loot : context.getLoots()) {
                CustomLoot.@Nullable Reuse reuse = loot.getReuse();
                if (reuse == null) {
                    continue;
                }

                List<ItemStack> reuseResult = new ArrayList<>();
                CustomDropsProcessor.computeSingleItemReuse(reuse, reuseResult, itemStack);

                CustomLootProcessingContext reuseContext = context.withLoots(Collections.singletonList(loot));
                reuseResult.forEach(item -> loot.getRecipient().receive(reuseContext, item));
            }
        }
    }

    @Listener
    public void onFastHarvestCropsSpawn(SpawnEntityEvent.Custom event, @First BlockSnapshot blockSnapshot) {
        boolean isFastHarvesting = event.context().get(BouEventContextKeys.IS_FAST_HARVESTING).orElse(false);
        if (!isFastHarvesting) {
            return;
        }

        @Nullable CustomLootProcessingContext processingContext = event.context().get(BouEventContextKeys.CUSTOM_LOOT_PROCESSING_CONTEXT).orElse(null);
        if (processingContext == null) {
            return;
        }

        for (CustomLoot loot : processingContext.getLoots()) {
            CustomDropsProcessor.handleDropItemEvent(event, loot, processingContext.withLoots(Collections.singletonList(loot)));
        }
    }

    @Override
    public void reload() {
        blocksDropIdsMappingCache.clear();
        seedIdsMappingCache.clear();
        productIdsMappingCache.clear();
    }

    @Nullable
    private static ItemStack createItemStack(int age, int maxAge, int fortuneLevel, FastHarvestCrop cropConfig,
                                             DefaultedRegistryReference<ItemType> itemType, boolean decrementQuantity) {
        CropsAlgorithm algorithm = CropsAlgorithm.ALG_19;
        if (itemType == ItemTypes.NETHER_WART) {
            algorithm = CropsAlgorithm.NETHER_WART;
        }

        int quantity = algorithm.compute(cropConfig, age, maxAge, fortuneLevel);
        if (decrementQuantity) {
            quantity--;
        }

        if (quantity > 0) {
            return ItemStack.of(itemType, quantity);
        }

        return null;
    }

    private static void spawnItem(ServerLocation spawnLocation, ItemStack itemStack) {
        ItemStackSnapshot representedItem = itemStack.createSnapshot();
        Entity entity = spawnLocation.createEntity(EntityTypes.ITEM.get());
        entity.offer(Keys.ITEM_STACK_SNAPSHOT, representedItem);
        entity.offer(Keys.PICKUP_DELAY, Ticks.of(10));
        spawnLocation.spawnEntity(entity);
    }

    private static class CropDefinition {

        private final int maxAge;
        private final DefaultedRegistryReference<ItemType> seed;
        private final int seedCount;
        private final @Nullable DefaultedRegistryReference<ItemType> product;
        private final int productCount;

        private CropDefinition(int maxAge, DefaultedRegistryReference<ItemType> seed, int seedCount,
                               @Nullable DefaultedRegistryReference<ItemType> product, int productCount) {
            this.maxAge = maxAge;
            this.seed = seed;
            this.seedCount = seedCount;
            this.product = product;
            this.productCount = productCount;
        }
    }
}
