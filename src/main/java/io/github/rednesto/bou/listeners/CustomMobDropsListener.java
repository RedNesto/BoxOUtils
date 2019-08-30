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

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

public class CustomMobDropsListener {

    private final Cache<UUID, Boolean> requirementResultsTracker = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build();

    @Listener
    public void onMobDeath(DestructEntityEvent.Death event) {
        Living targetEntity = event.getTargetEntity();
        Config.MobsDrops mobsDrops = Config.getMobsDrops();
        if (!mobsDrops.enabled) {
            return;
        }

        Map<String, CustomLoot> drops = mobsDrops.drops;
        CustomLoot loot = drops.get(targetEntity.getType().getId());
        if (loot == null) {
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

        EntitySnapshot targetEntitySnapshot = targetEntity.createSnapshot();
        boolean requirementsFulfilled = CustomDropsProcessor.fulfillsRequirements(targetEntitySnapshot, event.getCause(), loot.getRequirements());
        requirementResultsTracker.put(targetEntity.getUniqueId(), requirementsFulfilled);
        if (requirementsFulfilled) {
            Location<World> targetLocation = targetEntity.getLocation();
            CustomDropsProcessor.dropLoot(loot, player, targetLocation, targetEntitySnapshot, event.getCause());
        }
    }

    @Listener
    public void onItemDrop(DropItemEvent.Destruct event, @First Entity entity) {
        final Config.MobsDrops mobsDrops = Config.getMobsDrops();
        if (!mobsDrops.enabled) {
            return;
        }

        UUID entityId = entity.getUniqueId();
        Boolean result = requirementResultsTracker.getIfPresent(entityId);
        requirementResultsTracker.invalidate(entityId);
        if (result == null || !result) {
            return;
        }

        Map<String, CustomLoot> drops = mobsDrops.drops;
        CustomLoot customLoot = drops.get(entity.getType().getId());
        if (customLoot != null) {
            CustomDropsProcessor.handleDropItemEvent(event, customLoot, entity.createSnapshot());
        }
    }

    @Listener
    public void onExpOrbSpawn(SpawnEntityEvent event) {
        final Config.MobsDrops mobsDrops = Config.getMobsDrops();
        if (!mobsDrops.enabled) {
            return;
        }

        for (Entity entity : event.getEntities()) {
            if (!(entity instanceof ExperienceOrb)) {
                continue;
            }

            for (Object cause : event.getCause().noneOf(ExperienceOrb.class)) {
                if (cause instanceof Entity) {
                    Map<String, CustomLoot> drops = mobsDrops.drops;
                    CustomLoot customLoot = drops.get(((Entity) cause).getType().getId());
                    if (customLoot != null && customLoot.isExpOverwrite()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
