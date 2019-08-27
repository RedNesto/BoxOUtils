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
import io.github.rednesto.bou.api.fastharvest.FastHarvestCrop;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static io.github.rednesto.bou.Config.FastHarvest;

public class FastHarvestListener {

    private static final Map<String, CropDefinition> DEFINITIONS = new HashMap<>();

    static {
        DEFINITIONS.put("minecraft:wheat", new CropDefinition(7, ItemTypes.WHEAT_SEEDS, 3, ItemTypes.WHEAT, 0));
        DEFINITIONS.put("minecraft:carrots", new CropDefinition(7, ItemTypes.CARROT, 3, null, 0));
        DEFINITIONS.put("minecraft:potatoes", new CropDefinition(7, ItemTypes.POTATO, 3, null, 0));
        DEFINITIONS.put("minecraft:beetroots", new CropDefinition(3, ItemTypes.BEETROOT_SEEDS, 3, ItemTypes.BEETROOT, 0));
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

        FastHarvestCrop seedConfig;
        FastHarvestCrop productConfig = null;
        ItemType productType = cropDefinition.product;
        Config.CropsControl cropsControl = Config.getCropsControl();
        if (cropsControl.enabled) {
            seedConfig = cropsControl.crops.get(cropDefinition.seed.getId());
            if (productType != null) {
                productConfig = cropsControl.crops.get(productType.getId());
            }
        } else {
            seedConfig = FastHarvestCrop.createDefault(cropDefinition.seedCount);
            if (productType != null) {
                productConfig = FastHarvestCrop.createDefault(cropDefinition.productCount);
            }
        }

        Location<World> entitiesSpawnLocation = targetBlock.getLocation().orElse(player.getLocation());
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
                    player, fastHarvest.dropInWorld, entitiesSpawnLocation);
        }

        if (productType != null && productConfig != null) {
            process(age, maxAge, fortuneLevel, productConfig, productType, false,
                    player, fastHarvest.dropInWorld, entitiesSpawnLocation);
        }

        targetBlock.getLocation().ifPresent(location -> {
            BlockState oldState = targetBlock.getState();
            BlockState newState = oldState.with(Keys.GROWTH_STAGE, 0).orElse(oldState);
            location.setBlock(newState);
        });
    }

    private static void process(int age, int maxAge, int fortuneLevel, FastHarvestCrop cropConfig,
                                ItemType itemType, boolean decrementQuantity, Player player, boolean spawnInWorld,
                                Location<World> spawnLocation) {
        ItemStack itemStack = createItemStack(age, maxAge, fortuneLevel, cropConfig, itemType, decrementQuantity);
        if (spawnInWorld) {
            spawnItem(spawnLocation, itemStack);
        } else {
            player.getInventory().offer(itemStack);
        }
    }

    private static ItemStack createItemStack(int age, int maxAge, int fortuneLevel, FastHarvestCrop cropConfig,
                                             ItemType itemType, boolean decrementQuantity) {
        int quantity = CropsAlgoritm.ALG_19.compute(cropConfig, age, maxAge, fortuneLevel);
        if (decrementQuantity) {
            quantity--;
        }

        if (quantity > 0) {
            return ItemStack.of(itemType, quantity);
        }

        return ItemStack.empty();
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
