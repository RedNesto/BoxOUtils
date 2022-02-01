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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.projectile.source.ProjectileSource;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CustomMobDropsListener implements SpongeConfig.ReloadableListener {

    private final Cache<UUID, List<CustomLoot>> requirementResultsTracker = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build();
    private final IdSelector.Cache idsMappingCache = new IdSelector.Cache();

    @Listener
    public void onMobDeath(DestructEntityEvent.Death event) {
        Living targetEntity = event.entity();
        Config.MobsDrops mobsDrops = Config.getMobsDrops();
        if (!mobsDrops.enabled) {
            return;
        }

        Map<String, List<CustomLoot>> drops = mobsDrops.drops;
        ResourceKey originalKey = RegistryTypes.ENTITY_TYPE.keyFor(Sponge.game(), targetEntity.type());
        @Nullable List<CustomLoot> loots = idsMappingCache.get(drops, originalKey.formatted());
        if (loots == null) {
            return;
        }

        @Nullable ServerPlayer player = event.cause().first(ServerPlayer.class).orElseGet(() -> {
            Optional<IndirectEntityDamageSource> maybeIndirectSource = event.cause().first(IndirectEntityDamageSource.class);
            if (maybeIndirectSource.isPresent()) {
                Entity indirectSource = maybeIndirectSource.get().indirectSource();
                if (indirectSource instanceof ServerPlayer) {
                    return (ServerPlayer) indirectSource;
                }
            }

            Optional<EntityDamageSource> maybeEntitySource = event.cause().first(EntityDamageSource.class);
            if (maybeEntitySource.isPresent()) {
                Entity source = maybeEntitySource.get().source();
                if (source instanceof ServerPlayer) {
                    return (ServerPlayer) source;
                }

                if (source instanceof Projectile) {
                    @Nullable ProjectileSource shooter = ((Projectile) source).shooter().map(Value::get).orElse(null);
                    if (shooter instanceof ServerPlayer) {
                        return (ServerPlayer) shooter;
                    }
                }
            }

            return null;
        });

        EntitySnapshot targetEntitySnapshot = targetEntity.createSnapshot();
        ServerLocation targetLocation = targetEntity.serverLocation();
        CustomLootProcessingContext processingContext = new CustomLootProcessingContext(Collections.emptyList(), event, targetEntitySnapshot, event.cause(), player, targetLocation);
        List<CustomLoot> lootsToUse = CustomDropsProcessor.getLootsToUse(loots, processingContext);
        if (lootsToUse.isEmpty()) {
            return;
        }

        requirementResultsTracker.put(targetEntity.uniqueId(), lootsToUse);
        CustomDropsProcessor.dropLoot(processingContext.withLoots(lootsToUse));
    }

    @Listener
    public void onItemDrop(SpawnEntityEvent.Pre event, @First Entity entity) {
        if (!Config.getMobsDrops().enabled) {
            return;
        }

        UUID entityId = entity.uniqueId();
        @Nullable List<CustomLoot> customLoots = requirementResultsTracker.getIfPresent(entityId);
        if (customLoots != null) {
            customLoots.forEach(loot -> {
                @Nullable ServerPlayer targetPlayer = event.cause().first(ServerPlayer.class).orElse(null);
                CustomLootProcessingContext context = new CustomLootProcessingContext(Collections.singletonList(loot), event, entity.createSnapshot(), event.cause(), targetPlayer, entity.serverLocation());
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

        @Nullable List<CustomLoot> customLoot = requirementResultsTracker.getIfPresent(killedEntity.uniqueId());
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
