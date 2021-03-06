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
import io.github.rednesto.bou.IdSelector;
import io.github.rednesto.bou.SpongeConfig;
import io.github.rednesto.bou.SpongeUtils;
import io.github.rednesto.bou.api.blockspawners.SpawnedMob;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;

public class BlockSpawnersListener implements SpongeConfig.ReloadableListener {

    private final IdSelector.Cache idsMappingCache = new IdSelector.Cache();

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player) {
        final Config.BlockSpawners blockSpawners = Config.getBlockSpawners();
        if (!blockSpawners.enabled) {
            return;
        }

        Map<String, List<SpawnedMob>> spawners = blockSpawners.spawners;
        event.getTransactions().forEach(transaction -> {
            List<SpawnedMob> toSpawnMobs = idsMappingCache.get(spawners, transaction.getOriginal().getState().getType().getId());
            if (toSpawnMobs == null) {
                return;
            }

            Location<World> spawnLocation = SpongeUtils.getCenteredLocation(transaction.getOriginal(), player.getWorld());
            toSpawnMobs.forEach(toSpawn -> {
                if (!toSpawn.shouldSpawn()) {
                    return;
                }

                EntityType entityType = Sponge.getRegistry().getType(EntityType.class, toSpawn.getId()).orElse(null);
                if (entityType == null) {
                    return;
                }

                int quantityToSpawn = toSpawn.getQuantity() != null ? toSpawn.getQuantity().get() : 1;
                for (int i = 0; i < quantityToSpawn; i++) {
                    spawnLocation.spawnEntity(spawnLocation.createEntity(entityType));
                }
            });
        });
    }

    @Override
    public void reload() {
        idsMappingCache.clear();
    }
}
