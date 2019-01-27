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

import static io.github.rednesto.bou.common.Config.*;

public class FastHarvestListener {

    // TODO find a way to cleanup this file, this is an horrible mess right now
    // TODO support cocoa beans (and chorus maybe ?)

    @Listener
    public void onSecondaryClick(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
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
                int seedQuantity = CropsAlgoritm.ALG_19.compute(
                        age,
                        7,
                        SEED_DROP_MINIMUM,
                        SEED_DROP_COUNT,
                        level,
                        SEED_DROP_FORTUNE_FACTOR,
                        SEED_DROP_CHANCE,
                        SEED_DROP_CHANCE_OF) - 1;
                if(seedQuantity > 0) {
                    seed.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.WHEAT_SEEDS, seedQuantity).createSnapshot());
                    player.getWorld().spawnEntity(seed);
                }

                Entity wheat = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
                int wheatQuantity = CropsAlgoritm.ALG_19.compute(
                        age,
                        7,
                        WHEAT_DROP_MINIMUM,
                        WHEAT_DROP_COUNT,
                        level,
                        WHEAT_DROP_FORTUNE_FACTOR,
                        WHEAT_DROP_CHANCE,
                        WHEAT_DROP_CHANCE_OF);
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
                carrot.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.CARROT,
                        CropsAlgoritm.ALG_19.compute(
                                age,
                                7,
                                CARROT_DROP_MINIMUM,
                                CARROT_DROP_COUNT,
                                level,
                                CARROT_DROP_FORTUNE_FACTOR,
                                CARROT_DROP_CHANCE,
                                CARROT_DROP_CHANCE_OF) - 1).createSnapshot());
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
                potato.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.POTATO,
                        CropsAlgoritm.ALG_19.compute(
                                age,
                                7,
                                POTATO_DROP_MINIMUM,
                                POTATO_DROP_COUNT,
                                level,
                                POTATO_DROP_FORTUNE_FACTOR,
                                POTATO_DROP_CHANCE,
                                POTATO_DROP_CHANCE_OF) - 1).createSnapshot());
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
                beetrootSeed.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.BEETROOT_SEEDS,
                        CropsAlgoritm.ALG_19.compute(
                                age,
                                3,
                                BEETROOT_SEED_DROP_MINIMUM,
                                BEETROOT_SEED_DROP_COUNT,
                                level,
                                BEETROOT_SEED_DROP_FORTUNE_FACTOR,
                                BEETROOT_SEED_DROP_CHANCE,
                                BEETROOT_SEED_DROP_CHANCE_OF) - 1).createSnapshot());
                player.getWorld().spawnEntity(beetrootSeed);

                Entity beetroot = player.getWorld().createEntity(EntityTypes.ITEM, event.getTargetBlock().getLocation().orElse(player.getLocation()).getPosition());
                maybeEnchantements = event.getContext().get(EventContextKeys.USED_ITEM).get().get(Keys.ITEM_ENCHANTMENTS);
                level = maybeEnchantements.isPresent() ? maybeEnchantements.get().stream()
                        .filter(enchantment -> enchantment.getType().equals(EnchantmentTypes.FORTUNE))
                        .map(Enchantment::getLevel)
                        .findFirst().orElse(0) : 0;
                age = event.getTargetBlock().get(Keys.GROWTH_STAGE).orElse(0);
                beetroot.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.BEETROOT,
                        CropsAlgoritm.ALG_19.compute(
                                age,
                                3,
                                BEETROOT_DROP_MINIMUM,
                                BEETROOT_DROP_COUNT,
                                level,
                                BEETROOT_DROP_FORTUNE_FACTOR,
                                BEETROOT_DROP_CHANCE,
                                BEETROOT_DROP_CHANCE_OF)).createSnapshot());
                player.getWorld().spawnEntity(beetroot);

                event.getTargetBlock().getLocation().ifPresent(location -> location.setBlock(event.getTargetBlock().getState().with(Keys.GROWTH_STAGE, 0).orElse(event.getTargetBlock().getState())));
                break;
        }
    }
}
