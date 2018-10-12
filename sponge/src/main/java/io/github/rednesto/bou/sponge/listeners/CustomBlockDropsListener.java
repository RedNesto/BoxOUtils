/*
 * MIT License
 *
 * Copyright (c) [year] [fullname]
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
import io.github.rednesto.bou.common.CustomLoot;
import io.github.rednesto.bou.sponge.BoxOUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

public class CustomBlockDropsListener {

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player) {
        event.getTransactions().forEach(transaction -> {
            CustomLoot loot = Config.CUSTOM_BLOCKS_DROPS.get(transaction.getOriginal().getState().getType().getId());

            if(loot != null) {
                if(loot.getExperience() > 0) {
                    Sponge.getServer().getWorld(transaction.getOriginal().getWorldUniqueId()).ifPresent(world -> {
                        Entity experienceOrb = world.createEntity(EntityTypes.EXPERIENCE_ORB, transaction.getOriginal().getLocation().orElse(player.getLocation()).getPosition());
                        experienceOrb.offer(Keys.CONTAINED_EXPERIENCE, loot.getExperience());
                        world.spawnEntity(experienceOrb);
                    });
                }

                loot.getItemLoots().forEach(itemLoot -> {
                    switch(itemLoot.getType()) {
                        case CLASSIC:
                            if (itemLoot.shouldLoot()) {
                                Sponge.getServer().getWorld(transaction.getOriginal().getWorldUniqueId()).ifPresent(world -> {
                                    Sponge.getRegistry().getType(ItemType.class, itemLoot.getId()).ifPresent(itemType -> {
                                        Entity entity = world.createEntity(EntityTypes.ITEM, transaction.getOriginal().getLocation().orElse(player.getLocation()).getPosition());
                                        ItemStack itemStack = ItemStack.of(itemType, itemLoot.getQuantityToLoot());
                                        if (itemLoot.getDisplayname() != null)
                                            itemStack.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(itemLoot.getDisplayname()));

                                        entity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
                                        world.spawnEntity(entity);
                                    });
                                });
                            }
                            break;
                        case FILE_INVENTORIES:
                            BoxOUtils.getInstance().fileInvDo(integration -> integration.spawnBlockDrop(itemLoot, player, transaction.getOriginal()));
                            break;
                    }
                });
            }
        });
    }

    @Listener
    public void onItemDrop(DropItemEvent.Destruct event, @First Player player) {
        event.getCause().first(BlockSnapshot.class).ifPresent(block -> {
            CustomLoot customLoot = Config.CUSTOM_BLOCKS_DROPS.get(block.getState().getType().getId());
            if(customLoot != null && customLoot.isOverwrite()) {
                event.setCancelled(true);
            }
        });
    }

    @Listener
    public void onExpOrbSpawn(SpawnEntityEvent event) {
        event.getEntities().forEach(entity -> {
            if(entity instanceof ExperienceOrb) {
                event.getCause().noneOf(ExperienceOrb.class).forEach(it -> {
                    if(it instanceof BlockSnapshot) {
                        CustomLoot customLoot = Config.CUSTOM_BLOCKS_DROPS.get(((BlockSnapshot) it).getState().getType().getId());
                        if (customLoot != null && customLoot.isExpOverwrite())
                            event.setCancelled(true);
                    }
                });
            }
        });
    }
}
