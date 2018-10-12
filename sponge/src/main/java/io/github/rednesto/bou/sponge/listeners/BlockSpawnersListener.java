/*
 * MIT License
 *
 * Copyright (c) 2018 RedNesto
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
package io.github.rednesto.bou.sponge.listeners;

import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.SpawnedMob;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

import java.util.List;

public class BlockSpawnersListener {

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player) {
        event.getTransactions().forEach(transaction -> {
            List<SpawnedMob> spawnedMobs = Config.BLOCK_SPAWNERS_DROPS.get(transaction.getOriginal().getState().getType().getId());

            if (spawnedMobs != null) {
                spawnedMobs.forEach(spawnedMob -> {
                    if(spawnedMob.shouldSpawn()) {
                        Sponge.getServer().getWorld(transaction.getOriginal().getWorldUniqueId()).ifPresent(world -> {
                            Sponge.getRegistry().getType(EntityType.class, spawnedMob.getId()).ifPresent(entityType -> {
                                int quantityToSpawn = spawnedMob.getQuantityToSpawn();
                                for(int i = 0; i < quantityToSpawn; i++) {
                                    world.spawnEntity(world.createEntity(entityType, transaction.getOriginal().getPosition()));
                                }
                            });
                        });
                    }
                });
            }
        });
    }
}
