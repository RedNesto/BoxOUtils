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
import io.github.rednesto.bou.customdrops.CustomDropsProcessor;
import io.github.rednesto.bou.models.CustomLoot;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;

public class CustomBlockDropsListener {

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player) {
        Map<String, CustomLoot> drops = Config.getBlocksDrops().drops;
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot originalBlock = transaction.getOriginal();
            CustomLoot loot = drops.get(originalBlock.getState().getType().getId());
            if (loot == null) {
                continue;
            }

            Location<World> targetLocation = originalBlock.getLocation().orElse(player.getLocation());
            if (CustomDropsProcessor.fulfillsRequirements(originalBlock, event.getCause(), loot.getRequirements())) {
                CustomDropsProcessor.dropLoot(loot, player, targetLocation);
            }
        }
    }

    @Listener
    public void onItemDrop(DropItemEvent.Destruct event, @First Player player) {
        BlockSnapshot block = event.getCause().first(BlockSnapshot.class).orElse(null);
        if (block == null) {
            return;
        }

        Map<String, CustomLoot> drops = Config.getBlocksDrops().drops;
        CustomLoot customLoot = drops.get(block.getState().getType().getId());
        if (customLoot != null && CustomDropsProcessor.fulfillsRequirements(block, event.getCause(), customLoot.getRequirements())) {
            CustomDropsProcessor.handleDropItemEvent(event, customLoot);
        }
    }

    @Listener
    public void onExpOrbSpawn(SpawnEntityEvent event) {
        for (Entity entity : event.getEntities()) {
            if (!(entity instanceof ExperienceOrb)) {
                continue;
            }

            for (Object cause : event.getCause().noneOf(ExperienceOrb.class)) {
                if (cause instanceof BlockSnapshot) {
                    Map<String, CustomLoot> drops = Config.getBlocksDrops().drops;
                    CustomLoot customLoot = drops.get(((BlockSnapshot) cause).getState().getType().getId());
                    if (customLoot != null && customLoot.isExpOverwrite()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
