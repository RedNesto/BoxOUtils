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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.rednesto.bou.Config;
import io.github.rednesto.bou.CustomDropsProcessor;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
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
import java.util.concurrent.TimeUnit;

public class CustomBlockDropsListener {

    // I'd like to find a better thing to use as block identifier, something that does not rely on location,
    // entities unique id are great for that, but I don't know anything similar for blocks
    private final Cache<Location<World>, Boolean> requirementResultsTracker = CacheBuilder.newBuilder()
            // 15 seconds should be enough even if the server is skipping some ticks
            // but this should not be too high because the requirements test results may change quickly
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build();

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player) {
        Map<String, CustomLoot> drops = Config.getBlocksDrops().drops;
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot originalBlock = transaction.getOriginal();
            CustomLoot loot = drops.get(originalBlock.getState().getType().getId());
            if (loot == null) {
                continue;
            }

            boolean requirementsFulfilled = CustomDropsProcessor.fulfillsRequirements(originalBlock, event.getCause(), loot.getRequirements());
            Location<World> targetLocation = originalBlock.getLocation().orElseGet(() -> new Location<>(player.getWorld(), originalBlock.getPosition()));
            requirementResultsTracker.put(targetLocation, requirementsFulfilled);
            if (requirementsFulfilled) {
                CustomDropsProcessor.dropLoot(loot, player, targetLocation, originalBlock, event.getCause());
            }
        }
    }

    @Listener
    public void onItemDrop(DropItemEvent.Destruct event, @First Player player, @First BlockSnapshot block) {
        Location<World> targetLocation = block.getLocation().orElseGet(() -> new Location<>(player.getWorld(), block.getPosition()));
        Boolean result = requirementResultsTracker.getIfPresent(targetLocation);
        requirementResultsTracker.invalidate(targetLocation);
        if (result == null || !result) {
            return;
        }

        Map<String, CustomLoot> drops = Config.getBlocksDrops().drops;
        CustomLoot customLoot = drops.get(block.getState().getType().getId());
        if (customLoot != null) {
            CustomDropsProcessor.handleDropItemEvent(event, customLoot, block);
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
