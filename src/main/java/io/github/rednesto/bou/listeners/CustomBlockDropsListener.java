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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.ContextValue;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CustomBlockDropsListener implements SpongeConfig.ReloadableListener {

    // I'd like to find a better thing to use as block identifier, something that does not rely on location,
    // entities unique id are great for that, but I don't know anything similar for blocks
    private final Cache<ServerLocation, List<CustomLoot>> requirementResultsTracker = CacheBuilder.newBuilder()
            // 15 seconds should be enough even if the server is skipping some ticks
            // but this should not be too high because the requirements test results may change quickly
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build();
    private final IdSelector.Cache idsMappingCache = new IdSelector.Cache();

    @Listener
    public void onBlockBreak(ChangeBlockEvent.All event, @First ServerPlayer player) {
        final Config.BlocksDrops blocksDrops = Config.getBlocksDrops();
        if (!blocksDrops.enabled) {
            return;
        }

        Map<String, List<CustomLoot>> drops = blocksDrops.drops;
        event.transactions(Operations.BREAK.get()).forEach(transaction -> {
            BlockSnapshot original = transaction.original();
            ResourceKey originalKey = RegistryTypes.BLOCK_TYPE.keyFor(Sponge.game(), original.state().type());
            @Nullable List<CustomLoot> loots = idsMappingCache.get(drops, originalKey.formatted());
            if (loots == null) {
                return;
            }

            ServerLocation targetLocation = SpongeUtils.getCenteredLocation(original, player.world());
            CustomLootProcessingContext context = new CustomLootProcessingContext(Collections.emptyList(), event, original, event.cause(), player, targetLocation);
            List<CustomLoot> lootsToUse = CustomDropsProcessor.getLootsToUse(loots, context);
            if (!lootsToUse.isEmpty()) {
                requirementResultsTracker.put(targetLocation, lootsToUse);
                CustomDropsProcessor.dropLoot(context.withLoots(lootsToUse));
            }
        });
    }

    @Listener
    public void onItemDrop(SpawnEntityEvent.Pre event, @First ServerPlayer player, @ContextValue("BLOCK_HIT") BlockSnapshot block) {
        if (!Config.getBlocksDrops().enabled) {
            return;
        }

        ServerLocation targetLocation = SpongeUtils.getCenteredLocation(block, player.world());
        @Nullable List<CustomLoot> result = requirementResultsTracker.getIfPresent(targetLocation);
        if (result != null) {
            result.forEach(loot -> {
                CustomLootProcessingContext context = new CustomLootProcessingContext(Collections.singletonList(loot), event, block, event.cause(), player, targetLocation);
                CustomDropsProcessor.handleDropItemEvent(event, loot, context);
            });
        }
    }

    @Listener
    public void onExpOrbSpawn(SpawnEntityEvent event, @First BlockSnapshot brokenBlock) {
        if (!Config.getBlocksDrops().enabled) {
            return;
        }

        @Nullable List<CustomLoot> customLoot = brokenBlock.location()
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
