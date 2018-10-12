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
package io.github.rednesto.bou.spigot.events;

import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.CustomLoot;
import io.github.rednesto.bou.spigot.BoxOUtils;
import io.github.rednesto.bou.spigot.Utils;
import io.github.rednesto.fileinventories.api.FileInventories;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class CustomMobDropsListener implements Listener {

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        CustomLoot loot = Config.CUSTOM_MOBS_DROPS.get(event.getEntityType().name());
        if(loot != null) {
            if(loot.isOverwrite())
                event.getDrops().clear();

            if(loot.isExpOverwrite()) {
                event.setDroppedExp(0);
            }

            if(loot.getExperience() > 0) {
                Entity entity = event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.EXPERIENCE_ORB);
                ((ExperienceOrb) entity).setExperience(loot.getExperience());
            }

            loot.getItemLoots().forEach(itemLoot -> {
                switch(itemLoot.getType()) {
                    case CLASSIC:
                        try {
                            if(!itemLoot.shouldLoot())
                                break;

                            ItemStack itemStack = new ItemStack(Material.valueOf(itemLoot.getId()), itemLoot.getQuantityToLoot());
                            if (itemLoot.getDisplayname() != null) {
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                itemMeta.setDisplayName(Utils.applyColorCodes(itemLoot.getDisplayname()));
                                itemStack.setItemMeta(itemMeta);
                            }

                            event.getDrops().add(itemStack);
                        } catch (IllegalArgumentException e) {
                            BoxOUtils.getInstance().getLogger().warning("Material " + itemLoot.getId() + " does not exists");
                        }
                        break;
                    case FILE_INVENTORIES:
                        if(itemLoot.shouldLoot()) {
                            if (FileInventories.getService() == null) {
                                BoxOUtils.getInstance().getLogger().warning("The FileInventoriesService cannot be found. Has FileInventories been installed on this server?");
                                break;
                            }

                            Optional<ItemStack> maybeItem = FileInventories.getService().getItem(itemLoot.getId(), event.getEntity().getKiller());
                            if (!maybeItem.isPresent()) {
                                BoxOUtils.getInstance().getLogger().warning("The FileItem for ID " + itemLoot.getId() + " cannot be found");
                                break;
                            }

                            ItemStack itemStack = maybeItem.get();
                            int quantityToLoot = itemLoot.getQuantityToLoot();
                            if(quantityToLoot > itemStack.getAmount())
                                itemStack.setAmount(quantityToLoot);

                            event.getDrops().add(itemStack);
                        }
                        break;
                }
            });
        }
    }
}
