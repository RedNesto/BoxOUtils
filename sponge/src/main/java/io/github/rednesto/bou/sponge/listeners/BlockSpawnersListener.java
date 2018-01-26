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
