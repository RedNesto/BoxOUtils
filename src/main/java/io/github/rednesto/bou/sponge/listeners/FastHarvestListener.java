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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import static io.github.rednesto.bou.common.Config.FastHarvest;

public class FastHarvestListener {

    // TODO find a way to cleanup this file, this is an horrible mess right now
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

        CropDefinition cropDefinition = DEFINITIONS.get(event.getTargetBlock().getState().getType().getId());
        if (cropDefinition == null) {
            return;
        }

        ItemStack maybeItemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());
        if (!Config.canHarvest(maybeItemInHand.getType().getId()) || event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0) != cropDefinition.maxAge) {
            return;
        }

        FastHarvestCrop seedConfig = cropDefinition.seedConfigProvider.apply(fastHarvest);
        Entity seed = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
        Optional<List<Enchantment>> maybeEnchantements = event.getContext().get(EventContextKeys.USED_ITEM).get().get(Keys.ITEM_ENCHANTMENTS);
        int level = maybeEnchantements.isPresent() ? maybeEnchantements.get().stream()
                .filter(enchantment -> enchantment.getType().equals(EnchantmentTypes.FORTUNE))
                .map(Enchantment::getLevel)
                .findFirst().orElse(0) : 0;
        int age = event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0);
        int seedQuantity = CropsAlgoritm.ALG_19.compute(
                age,
                cropDefinition.maxAge,
                seedConfig.getMinimum(),
                seedConfig.getCount(),
                level,
                seedConfig.getFortuneFactor(),
                seedConfig.getChance(),
                seedConfig.getChanceOf()) - 1;
        if (seedQuantity > 0) {
            seed.offer(Keys.REPRESENTED_ITEM, ItemStack.of(cropDefinition.seed, seedQuantity).createSnapshot());
            player.getWorld().spawnEntity(seed);
        }

        Function<FastHarvest, FastHarvestCrop> productConfigProvider = cropDefinition.productConfigProvider;
        ItemType productType = cropDefinition.product;
        if (productConfigProvider != null && productType != null ) {
            FastHarvestCrop wheatConfig = productConfigProvider.apply(fastHarvest);
            int productQuantity = CropsAlgoritm.ALG_19.compute(
                    age,
                    cropDefinition.maxAge,
                    wheatConfig.getMinimum(),
                    wheatConfig.getCount(),
                    level,
                    wheatConfig.getFortuneFactor(),
                    wheatConfig.getChance(),
                    wheatConfig.getChanceOf());
            Entity product = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
            product.offer(Keys.REPRESENTED_ITEM, ItemStack.of(productType, productQuantity).createSnapshot());
            player.getWorld().spawnEntity(product);
        }

        event.getTargetBlock().getLocation().ifPresent(location -> location.setBlock(event.getTargetBlock().getState().with(Keys.GROWTH_STAGE, 0).orElse(event.getTargetBlock().getState())));
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
