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
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

import static io.github.rednesto.bou.common.Config.FastHarvest;

public class FastHarvestListener {

    // TODO find a way to cleanup this file, this is an horrible mess right now
    // TODO support cocoa beans (and chorus maybe ?)

    @Listener
    public void onSecondaryClick(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        FastHarvest fastHarvest = Config.getFastHarvest();
        if (!fastHarvest.enabled)
            return;

        switch(event.getTargetBlock().getState().getType().getId()) {
            case "minecraft:wheat":
                ItemStack maybeItemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());
                if(!Config.canHarvest(maybeItemInHand.getType().getId()) || event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0) != 7)
                    break;

                Entity seed = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
                Optional<List<Enchantment>> maybeEnchantements = event.getContext().get(EventContextKeys.USED_ITEM).get().get(Keys.ITEM_ENCHANTMENTS);
                int level = maybeEnchantements.isPresent() ? maybeEnchantements.get().stream()
                        .filter(enchantment -> enchantment.getType().equals(EnchantmentTypes.FORTUNE))
                        .map(Enchantment::getLevel)
                        .findFirst().orElse(0) : 0;
                int age = event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0);
                FastHarvestCrop seedConfig = fastHarvest.seed;
                int seedQuantity = CropsAlgoritm.ALG_19.compute(
                        age,
                        7,
                        seedConfig.getMinimum(),
                        seedConfig.getCount(),
                        level,
                        seedConfig.getFortuneFactor(),
                        seedConfig.getChance(),
                        seedConfig.getChanceOf()) - 1;
                if(seedQuantity > 0) {
                    seed.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.WHEAT_SEEDS, seedQuantity).createSnapshot());
                    player.getWorld().spawnEntity(seed);
                }

                Entity wheat = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
                FastHarvestCrop wheatConfig = fastHarvest.wheat;
                int wheatQuantity = CropsAlgoritm.ALG_19.compute(
                        age,
                        7,
                        wheatConfig.getMinimum(),
                        wheatConfig.getCount(),
                        level,
                        wheatConfig.getFortuneFactor(),
                        wheatConfig.getChance(),
                        wheatConfig.getChanceOf());
                wheat.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.WHEAT, wheatQuantity).createSnapshot());
                player.getWorld().spawnEntity(wheat);

                event.getTargetBlock().getLocation().ifPresent(location -> location.setBlock(event.getTargetBlock().getState().with(Keys.GROWTH_STAGE, 0).orElse(event.getTargetBlock().getState())));
                break;
            case "minecraft:carrots":
                maybeItemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());
                if(!Config.canHarvest(maybeItemInHand.getType().getId()) || event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0) != 7)
                    break;

                Entity carrot = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
                maybeEnchantements = event.getContext().get(EventContextKeys.USED_ITEM).get().get(Keys.ITEM_ENCHANTMENTS);
                level = maybeEnchantements.isPresent() ? maybeEnchantements.get().stream()
                        .filter(enchantment -> enchantment.getType().equals(EnchantmentTypes.FORTUNE))
                        .map(Enchantment::getLevel)
                        .findFirst().orElse(0) : 0;
                age = event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0);
                FastHarvestCrop carrotConfig = fastHarvest.carrot;
                carrot.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.CARROT,
                        CropsAlgoritm.ALG_19.compute(
                                age,
                                7,
                                carrotConfig.getMinimum(),
                                carrotConfig.getCount(),
                                level,
                                carrotConfig.getFortuneFactor(),
                                carrotConfig.getChance(),
                                carrotConfig.getChanceOf()) - 1).createSnapshot());
                player.getWorld().spawnEntity(carrot);

                event.getTargetBlock().getLocation().ifPresent(location -> location.setBlock(event.getTargetBlock().getState().with(Keys.GROWTH_STAGE, 0).orElse(event.getTargetBlock().getState())));
                break;
            case "minecraft:potatoes":
                maybeItemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());
                if(!Config.canHarvest(maybeItemInHand.getType().getId()) || event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0) != 7)
                    break;

                Entity potato = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
                maybeEnchantements = event.getContext().get(EventContextKeys.USED_ITEM).get().get(Keys.ITEM_ENCHANTMENTS);
                level = maybeEnchantements.isPresent() ? maybeEnchantements.get().stream()
                        .filter(enchantment -> enchantment.getType().equals(EnchantmentTypes.FORTUNE))
                        .map(Enchantment::getLevel)
                        .findFirst().orElse(0) : 0;
                age = event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0);
                FastHarvestCrop potatoConfig = fastHarvest.potato;
                potato.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.POTATO,
                        CropsAlgoritm.ALG_19.compute(
                                age,
                                7,
                                potatoConfig.getMinimum(),
                                potatoConfig.getCount(),
                                level,
                                potatoConfig.getFortuneFactor(),
                                potatoConfig.getChance(),
                                potatoConfig.getChanceOf()) - 1).createSnapshot());
                player.getWorld().spawnEntity(potato);

                event.getTargetBlock().getLocation().ifPresent(location -> location.setBlock(event.getTargetBlock().getState().with(Keys.GROWTH_STAGE, 0).orElse(event.getTargetBlock().getState())));
                break;
            case "minecraft:beetroots":
                maybeItemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());
                if(!Config.canHarvest(maybeItemInHand.getType().getId()) || event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0) != 3)
                    break;

                Entity beetrootSeed = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
                maybeEnchantements = event.getContext().get(EventContextKeys.USED_ITEM).get().get(Keys.ITEM_ENCHANTMENTS);
                level = maybeEnchantements.isPresent() ? maybeEnchantements.get().stream()
                        .filter(enchantment -> enchantment.getType().equals(EnchantmentTypes.FORTUNE))
                        .map(Enchantment::getLevel)
                        .findFirst().orElse(0) : 0;
                age = event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0);
                FastHarvestCrop beetrootSeedConfig = fastHarvest.beetrootSeed;
                beetrootSeed.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.BEETROOT_SEEDS,
                        CropsAlgoritm.ALG_19.compute(
                                age,
                                3,
                                beetrootSeedConfig.getMinimum(),
                                beetrootSeedConfig.getCount(),
                                level,
                                beetrootSeedConfig.getFortuneFactor(),
                                beetrootSeedConfig.getChance(),
                                beetrootSeedConfig.getChanceOf()) - 1).createSnapshot());
                player.getWorld().spawnEntity(beetrootSeed);

                Entity beetroot = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
                maybeEnchantements = event.getContext().get(EventContextKeys.USED_ITEM).get().get(Keys.ITEM_ENCHANTMENTS);
                level = maybeEnchantements.isPresent() ? maybeEnchantements.get().stream()
                        .filter(enchantment -> enchantment.getType().equals(EnchantmentTypes.FORTUNE))
                        .map(Enchantment::getLevel)
                        .findFirst().orElse(0) : 0;
                age = event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0);
                FastHarvestCrop beetrootConfig = fastHarvest.beetroot;
                beetroot.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.BEETROOT,
                        CropsAlgoritm.ALG_19.compute(
                                age,
                                3,
                                beetrootConfig.getMinimum(),
                                beetrootConfig.getCount(),
                                level,
                                beetrootConfig.getFortuneFactor(),
                                beetrootConfig.getChance(),
                                beetrootConfig.getChanceOf())).createSnapshot());
                player.getWorld().spawnEntity(beetroot);

                event.getTargetBlock().getLocation().ifPresent(location -> location.setBlock(event.getTargetBlock().getState().with(Keys.GROWTH_STAGE, 0).orElse(event.getTargetBlock().getState())));
                break;
        }
    }
}
