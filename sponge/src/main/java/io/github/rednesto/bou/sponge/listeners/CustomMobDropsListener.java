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
import io.github.rednesto.fileinventories.api.FileInventoriesService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class CustomMobDropsListener {

    @Listener
    public void onMobDeath(DestructEntityEvent.Death event, @First Player player) {
        CustomLoot loot = Config.CUSTOM_MOBS_DROPS.get(event.getTargetEntity().getType().getId());

        if(loot != null) {
            if(loot.getExperience() > 0) {
                World world = event.getTargetEntity().getWorld();
                Entity experienceOrb = world.createEntity(EntityTypes.EXPERIENCE_ORB, event.getTargetEntity().getLocation().getPosition());
                experienceOrb.offer(Keys.CONTAINED_EXPERIENCE, loot.getExperience());
                world.spawnEntity(experienceOrb);
            }

            loot.getItemLoots().forEach(itemLoot -> {
                switch(itemLoot.getType()) {
                    case CLASSIC:
                        if(itemLoot.shouldLoot()) {
                            Sponge.getRegistry().getType(ItemType.class, itemLoot.getId()).ifPresent(itemType -> {
                                Entity entity = event.getTargetEntity().getWorld().createEntity(EntityTypes.ITEM, event.getTargetEntity().getLocation().getPosition());
                                entity.offer(Keys.REPRESENTED_ITEM, ItemStack.builder().itemType(itemType).quantity(itemLoot.getQuantityToLoot()).build().createSnapshot());
                                event.getTargetEntity().getWorld().spawnEntity(entity);
                            });
                        }
                        break;
                    case FILE_INVENTORIES:
                        Optional<FileInventoriesService> maybeService = Sponge.getServiceManager().provide(FileInventoriesService.class);
                        if(!maybeService.isPresent()) {
                            BoxOUtils.getInstance().getLogger().warn("The FileInventoriesService cannot be found. Has FileInventories been installed on this server?");
                            break;
                        }

                        Optional<ItemStack> maybeItem = maybeService.get().getItem(itemLoot.getId(), player);
                        if(!maybeItem.isPresent()) {
                            BoxOUtils.getInstance().getLogger().warn("The FileItem for ID " + itemLoot.getId() + " cannot be found");
                            break;
                        }

                        if(itemLoot.shouldLoot()) {
                            Entity entity = event.getTargetEntity().getWorld().createEntity(EntityTypes.ITEM, event.getTargetEntity().getLocation().getPosition());

                            ItemStack itemStack = maybeItem.get();
                            int quantityToLoot = itemLoot.getQuantityToLoot();
                            if(quantityToLoot > itemStack.getQuantity())
                                itemStack.setQuantity(quantityToLoot);

                            entity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
                            event.getTargetEntity().getWorld().spawnEntity(entity);
                        }
                        break;
                }
            });
        }
    }

    @Listener
    public void onItemDrop(DropItemEvent.Destruct event) {
        event.getCause().first(Entity.class).ifPresent(entity -> {
            if(Config.CUSTOM_MOBS_DROPS.containsKey(entity.getType().getId()) && Config.CUSTOM_MOBS_DROPS.get(entity.getType().getId()).isOverwrite())
                event.setCancelled(true);
        });
    }

    @Listener
    public void onExpOrbSpawn(SpawnEntityEvent event) {
        event.getEntities().forEach(entity -> {
            if(entity instanceof ExperienceOrb) {
                event.getCause().noneOf(ExperienceOrb.class).forEach(it -> {
                    if(it instanceof Entity) {
                        CustomLoot customLoot = Config.CUSTOM_MOBS_DROPS.get(((Entity) it).getType().getId());
                        if (customLoot != null && customLoot.isExpOverwrite()) {
                            event.setCancelled(true);
                        }
                    }
                });
            }
        });
    }
}
