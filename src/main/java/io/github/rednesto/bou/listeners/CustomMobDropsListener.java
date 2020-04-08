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
import io.github.rednesto.bou.IdSelector;
import io.github.rednesto.bou.SpongeConfig;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

public class CustomMobDropsListener implements SpongeConfig.ReloadableListener {

    private final Cache<UUID, List<CustomLoot>> requirementResultsTracker = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build();
    private final IdSelector.Cache idsMappingCache = new IdSelector.Cache();

    @Listener
    public void onMobDeath(DestructEntityEvent.Death event) {
        Living targetEntity = event.getTargetEntity();
        Config.MobsDrops mobsDrops = Config.getMobsDrops();
        if (!mobsDrops.enabled) {
            return;
        }

        Map<String, List<CustomLoot>> drops = mobsDrops.drops;
        List<CustomLoot> loots = idsMappingCache.get(drops, targetEntity.getType().getId());
        if (loots == null) {
            return;
        }

        EntitySnapshot targetEntitySnapshot = targetEntity.createSnapshot();
        List<CustomLoot> lootsToUse = CustomDropsProcessor.getLootsToUse(loots, targetEntitySnapshot, event.getCause());
        if (lootsToUse.isEmpty()) {
            return;
        }

        @Nullable Player player = event.getCause().first(Player.class).orElseGet(() -> {
            Optional<IndirectEntityDamageSource> maybeIndirectSource = event.getCause().first(IndirectEntityDamageSource.class);
            if (maybeIndirectSource.isPresent()) {
                Entity indirectSource = maybeIndirectSource.get().getIndirectSource();
                if (indirectSource instanceof Player) {
                    return (Player) indirectSource;
                }
            }

            return null;
        });

        requirementResultsTracker.put(targetEntity.getUniqueId(), lootsToUse);
        Location<World> targetLocation = targetEntity.getLocation();
        CustomLootProcessingContext processingContext = new CustomLootProcessingContext(lootsToUse, event, targetEntitySnapshot, event.getCause(), player, targetLocation);
        CustomDropsProcessor.dropLoot(processingContext);
    }

    @Listener
    public void onItemDrop(DropItemEvent.Destruct event, @First Entity entity) {
        if (!Config.getMobsDrops().enabled) {
            return;
        }

        UUID entityId = entity.getUniqueId();
        List<CustomLoot> customLoots = requirementResultsTracker.getIfPresent(entityId);
        if (customLoots != null) {
            customLoots.forEach(loot -> {
                Player targetPlayer = event.getCause().first(Player.class).orElse(null);
                CustomLootProcessingContext context = new CustomLootProcessingContext(Collections.singletonList(loot), event, entity.createSnapshot(), event.getCause(), targetPlayer, entity.getLocation());
                CustomDropsProcessor.handleDropItemEvent(event, loot, context);
            });
        }
    }

    @Listener
    public void onExpOrbSpawn(SpawnEntityEvent event,
                              @First(typeFilter = ExperienceOrb.class, inverse = true) Entity killedEntity) {
        if (!Config.getMobsDrops().enabled) {
            return;
        }

        List<CustomLoot> customLoot = requirementResultsTracker.getIfPresent(killedEntity.getUniqueId());
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
