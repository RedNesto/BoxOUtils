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
package io.github.rednesto.bou.sponge.listeners;

import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.CropsAlgoritm;
import io.github.rednesto.bou.common.FastHarvestCrop;
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
import java.util.function.Function;

import javax.annotation.Nullable;

import static io.github.rednesto.bou.common.Config.FastHarvest;

public class FastHarvestListener {

    // TODO support cocoa beans (and chorus maybe ?)

    private static final Map<String, CropDefinition> DEFINITIONS = new HashMap<>();

    static {
        DEFINITIONS.put("minecraft:wheat", new CropDefinition(7,
                config -> config.seed, ItemTypes.WHEAT_SEEDS, config -> config.wheat, ItemTypes.WHEAT));
        DEFINITIONS.put("minecraft:carrots", new CropDefinition(7,
                config -> config.carrot, ItemTypes.CARROT, null, null));
        DEFINITIONS.put("minecraft:potatoes", new CropDefinition(7,
                config -> config.potato, ItemTypes.POTATO, null, null));
        DEFINITIONS.put("minecraft:beetroots", new CropDefinition(3,
                config -> config.beetrootSeed, ItemTypes.BEETROOT_SEEDS, config -> config.beetroot, ItemTypes.BEETROOT));
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

        ItemStack maybeItemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());
        int age = targetBlock.get(Keys.GROWTH_STAGE).orElse(0);
        if (!Config.canHarvest(maybeItemInHand.getType().getId()) || age != cropDefinition.maxAge) {
            return;
        }

        int fortuneLevel = event.getContext().get(EventContextKeys.USED_ITEM)
                .flatMap(item -> item.get(Keys.ITEM_ENCHANTMENTS))
                .flatMap(enchantments -> enchantments.stream()
                        .filter(enchantment -> enchantment.getType().equals(EnchantmentTypes.FORTUNE))
                        .map(Enchantment::getLevel)
                        .findFirst())
                .orElse(0);

        Location<World> entitiesSpawnLocation = targetBlock.getLocation().orElse(player.getLocation());

        FastHarvestCrop seedConfig = cropDefinition.seedConfigProvider.apply(fastHarvest);
        // We decrement because we consume the seed to plant it again
        createAndSpawnEntity(cropDefinition, age, fortuneLevel, entitiesSpawnLocation, seedConfig, cropDefinition.seed, true);

        Function<FastHarvest, FastHarvestCrop> productConfigProvider = cropDefinition.productConfigProvider;
        ItemType productType = cropDefinition.product;
        if (productConfigProvider != null && productType != null ) {
            FastHarvestCrop productConfig = productConfigProvider.apply(fastHarvest);
            createAndSpawnEntity(cropDefinition, age, fortuneLevel, entitiesSpawnLocation, productConfig, cropDefinition.product, false);
        }

        targetBlock.getLocation().ifPresent(location -> {
            BlockState oldState = targetBlock.getState();
            BlockState newState = oldState.with(Keys.GROWTH_STAGE, 0).orElse(oldState);
            location.setBlock(newState);
        });
    }

    private void createAndSpawnEntity(CropDefinition cropDefinition, int age, int fortuneLevel,
                                     Location<World> entitiesSpawnLocation, FastHarvestCrop cropConfig, ItemType itemType,
                                     boolean decrementQuantity) {
        int quantity = CropsAlgoritm.ALG_19.compute(cropConfig, age, cropDefinition.maxAge, fortuneLevel);
        if (decrementQuantity) {
            quantity--;
        }

        if (quantity > 0) {
            ItemStackSnapshot representedItem = ItemStack.of(itemType, quantity).createSnapshot();
            Entity entity = entitiesSpawnLocation.createEntity(EntityTypes.ITEM);
            entity.offer(Keys.REPRESENTED_ITEM, representedItem);
            entitiesSpawnLocation.spawnEntity(entity);
        }
    }

    private static class CropDefinition {

        private final int maxAge;
        private final Function<FastHarvest, FastHarvestCrop> seedConfigProvider;
        private final ItemType seed;
        @Nullable
        private final Function<FastHarvest, FastHarvestCrop> productConfigProvider;
        @Nullable
        private final ItemType product;

        private CropDefinition(int maxAge, Function<FastHarvest, FastHarvestCrop> seedConfigProvider, ItemType seed,
                               @Nullable Function<FastHarvest, FastHarvestCrop> productConfigProvider, @Nullable ItemType product) {
            this.maxAge = maxAge;
            this.seedConfigProvider = seedConfigProvider;
            this.seed = seed;
            this.productConfigProvider = productConfigProvider;
            this.product = product;
        }
    }
}
