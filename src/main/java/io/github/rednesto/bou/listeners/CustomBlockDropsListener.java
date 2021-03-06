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
import io.github.rednesto.bou.*;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CustomBlockDropsListener implements SpongeConfig.ReloadableListener {

    // I'd like to find a better thing to use as block identifier, something that does not rely on location,
    // entities unique id are great for that, but I don't know anything similar for blocks
    private final Cache<Location<World>, List<CustomLoot>> requirementResultsTracker = CacheBuilder.newBuilder()
            // 15 seconds should be enough even if the server is skipping some ticks
            // but this should not be too high because the requirements test results may change quickly
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build();
    private final IdSelector.Cache idsMappingCache = new IdSelector.Cache();

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player) {
        final Config.BlocksDrops blocksDrops = Config.getBlocksDrops();
        if (!blocksDrops.enabled) {
            return;
        }

        Map<String, List<CustomLoot>> drops = blocksDrops.drops;
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot originalBlock = transaction.getOriginal();
            List<CustomLoot> loots = idsMappingCache.get(drops, originalBlock.getState().getType().getId());
            if (loots == null) {
                continue;
            }

            Location<World> targetLocation = SpongeUtils.getCenteredLocation(originalBlock, player.getWorld());
            CustomLootProcessingContext context = new CustomLootProcessingContext(Collections.emptyList(), event, originalBlock, event.getCause(), player, targetLocation);
            List<CustomLoot> lootsToUse = CustomDropsProcessor.getLootsToUse(loots, context);
            if (!lootsToUse.isEmpty()) {
                requirementResultsTracker.put(targetLocation, lootsToUse);
                CustomDropsProcessor.dropLoot(context.withLoots(lootsToUse));
            }
        }
    }

    @Listener
    public void onItemDrop(DropItemEvent.Destruct event, @First Player player, @First BlockSnapshot block) {
        if (!Config.getBlocksDrops().enabled) {
            return;
        }

        Location<World> targetLocation = SpongeUtils.getCenteredLocation(block, player.getWorld());
        List<CustomLoot> result = requirementResultsTracker.getIfPresent(targetLocation);
        if (result != null) {
            result.forEach(loot -> {
                CustomLootProcessingContext context = new CustomLootProcessingContext(Collections.singletonList(loot), event, block, event.getCause(), player, targetLocation);
                CustomDropsProcessor.handleDropItemEvent(event, loot, context);
            });
        }
    }

    @Listener
    public void onExpOrbSpawn(SpawnEntityEvent event, @First BlockSnapshot brokenBlock) {
        if (!Config.getBlocksDrops().enabled) {
            return;
        }

        List<CustomLoot> customLoot = brokenBlock.getLocation()
                .map(SpongeUtils::center)
                .map(requirementResultsTracker::getIfPresent)
                .orElse(null);
        if (customLoot != null) {
            for (CustomLoot loot : customLoot) {
                if (loot.isExpOverwrite()) {
                    event.filterEntities(entity -> !(entity instanceof ExperienceOrb));
                    break;
                }
            }
        }
    }

    @Override
    public void reload() {
        idsMappingCache.clear();
    }
}
