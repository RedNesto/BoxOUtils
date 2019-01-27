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
package io.github.rednesto.bou.spigot.events;

import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.CropsAlgoritm;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static io.github.rednesto.bou.common.Config.*;

public class FastHarvestListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(!Bukkit.getBukkitVersion().contains("1.8") && event.getHand() != EquipmentSlot.HAND)
            return;

        switch(event.getClickedBlock().getType()) {
            case CROPS:
                if(!Config.canHarvest(event.getMaterial().name()) || event.getClickedBlock().getData() != 7)
                    break;

                event.getClickedBlock().getWorld().dropItem(event.getClickedBlock().getLocation(),
                        new ItemStack(Material.SEEDS, Bukkit.getVersion().contains("1.8") ?
                                CropsAlgoritm.ALG_18.compute(
                                        event.getClickedBlock().getData(),
                                        7,
                                        SEED_DROP_MINIMUM,
                                        SEED_DROP_COUNT,
                                        event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                                        SEED_DROP_FORTUNE_FACTOR,
                                        SEED_DROP_CHANCE,
                                        SEED_DROP_CHANCE_OF) - 1
                                : CropsAlgoritm.ALG_19.compute(
                                event.getClickedBlock().getData(),
                                7,
                                SEED_DROP_MINIMUM,
                                SEED_DROP_COUNT,
                                event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                                SEED_DROP_FORTUNE_FACTOR,
                                SEED_DROP_CHANCE,
                                SEED_DROP_CHANCE_OF) - 1));

                event.getClickedBlock().getWorld().dropItem(event.getClickedBlock().getLocation(),
                        new ItemStack(Material.WHEAT, Bukkit.getVersion().contains("1.8") ? CropsAlgoritm.ALG_18.compute(
                                7,
                                7,
                                WHEAT_DROP_MINIMUM,
                                WHEAT_DROP_COUNT,
                                event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                                WHEAT_DROP_FORTUNE_FACTOR,
                                WHEAT_DROP_CHANCE,
                                WHEAT_DROP_CHANCE_OF)
                                : CropsAlgoritm.ALG_19.compute(
                                7,
                                7,
                                WHEAT_DROP_MINIMUM,
                                WHEAT_DROP_COUNT,
                                event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                                WHEAT_DROP_FORTUNE_FACTOR,
                                WHEAT_DROP_CHANCE,
                                WHEAT_DROP_CHANCE_OF)));
                event.getClickedBlock().setType(Material.CROPS);
                break;
            case CARROT:
                if(!Config.canHarvest(event.getMaterial().name()) || event.getClickedBlock().getData() != 7)
                    break;

                event.getClickedBlock().getWorld().dropItem(event.getClickedBlock().getLocation(),
                        new ItemStack(Material.CARROT_ITEM, Bukkit.getVersion().contains("1.8") ? CropsAlgoritm.ALG_18.compute(
                                7,
                                7,
                                CARROT_DROP_MINIMUM,
                                CARROT_DROP_COUNT,
                                event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                                CARROT_DROP_FORTUNE_FACTOR,
                                CARROT_DROP_CHANCE,
                                CARROT_DROP_CHANCE_OF) - 1
                                : CropsAlgoritm.ALG_19.compute(
                                7,
                                7,
                                CARROT_DROP_MINIMUM,
                                CARROT_DROP_COUNT,
                                event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                                CARROT_DROP_FORTUNE_FACTOR,
                                CARROT_DROP_CHANCE,
                                CARROT_DROP_CHANCE_OF) - 1));
                event.getClickedBlock().setType(Material.CARROT);
                break;
            case POTATO:
                if(!Config.canHarvest(event.getMaterial().name()) || event.getClickedBlock().getData() != 7)
                    break;

                event.getClickedBlock().getWorld().dropItem(event.getClickedBlock().getLocation(),
                        new ItemStack(Material.POTATO_ITEM, Bukkit.getVersion().contains("1.8") ? CropsAlgoritm.ALG_18.compute(
                                7,
                                7,
                                POTATO_DROP_MINIMUM,
                                POTATO_DROP_COUNT,
                                event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                                POTATO_DROP_FORTUNE_FACTOR,
                                POTATO_DROP_CHANCE,
                                POTATO_DROP_CHANCE_OF) - 1
                                : CropsAlgoritm.ALG_19.compute(
                                7,
                                7,
                                POTATO_DROP_MINIMUM,
                                POTATO_DROP_COUNT,
                                event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                                POTATO_DROP_FORTUNE_FACTOR,
                                POTATO_DROP_CHANCE,
                                POTATO_DROP_CHANCE_OF) - 1));
                event.getClickedBlock().setType(Material.POTATO);
                break;
        }

        if((Bukkit.getVersion().contains("1.9")
                || Bukkit.getVersion().contains("1.10")
                || Bukkit.getVersion().contains("1.11")
                || Bukkit.getVersion().contains("1.12"))
                && event.getClickedBlock().getType() == Material.BEETROOT_BLOCK && event.getClickedBlock().getData() == 3
                && Config.canHarvest(event.getMaterial().name())) {
            event.getClickedBlock().getWorld().dropItem(event.getClickedBlock().getLocation(),
                    new ItemStack(Material.BEETROOT_SEEDS, Bukkit.getVersion().contains("1.8") ? CropsAlgoritm.ALG_18.compute(
                            3,
                            3,
                            BEETROOT_SEED_DROP_MINIMUM,
                            BEETROOT_SEED_DROP_COUNT,
                            event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                            BEETROOT_SEED_DROP_FORTUNE_FACTOR,
                            BEETROOT_SEED_DROP_CHANCE,
                            BEETROOT_SEED_DROP_CHANCE_OF) - 1
                            : CropsAlgoritm.ALG_19.compute(
                            3,
                            3,
                            BEETROOT_SEED_DROP_MINIMUM,
                            BEETROOT_SEED_DROP_COUNT,
                            event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                            BEETROOT_SEED_DROP_FORTUNE_FACTOR,
                            BEETROOT_SEED_DROP_CHANCE,
                            BEETROOT_SEED_DROP_CHANCE_OF) - 1));

            event.getClickedBlock().getWorld().dropItem(event.getClickedBlock().getLocation(),
                    new ItemStack(Material.BEETROOT, Bukkit.getVersion().contains("1.8") ? CropsAlgoritm.ALG_18.compute(
                            3,
                            3,
                            BEETROOT_DROP_MINIMUM,
                            BEETROOT_DROP_COUNT,
                            event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                            BEETROOT_DROP_FORTUNE_FACTOR,
                            BEETROOT_DROP_CHANCE,
                            BEETROOT_DROP_CHANCE_OF)
                            : CropsAlgoritm.ALG_19.compute(
                            3,
                            3,
                            BEETROOT_DROP_MINIMUM,
                            BEETROOT_DROP_COUNT,
                            event.getItem() != null ? event.getItem().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0,
                            BEETROOT_DROP_FORTUNE_FACTOR,
                            BEETROOT_DROP_CHANCE,
                            BEETROOT_DROP_CHANCE_OF)));
            event.getClickedBlock().setType(Material.BEETROOT_BLOCK);
        }
    }
}
