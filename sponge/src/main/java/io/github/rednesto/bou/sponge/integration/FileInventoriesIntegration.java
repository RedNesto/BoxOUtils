package io.github.rednesto.bou.sponge.integration;

import io.github.rednesto.bou.common.ItemLoot;
import io.github.rednesto.bou.sponge.BoxOUtils;
import io.github.rednesto.fileinventories.api.FileInventoriesService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class FileInventoriesIntegration {

    public void loadItems() {
        Sponge.getServiceManager().provide(FileInventoriesService.class).ifPresent(service -> {
            Path fileitems = BoxOUtils.getInstance().getConfigDir().resolve("fileitems");
            if (Files.isDirectory(fileitems)) {
                try (Stream<Path> files = Files.walk(fileitems)) {
                    files.filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
                            .forEach(path -> {
                                try {
                                    service.load(FileInventoriesService.LoadTarget.ITEMS, path);
                                } catch (IOException e) {
                                    BoxOUtils.getInstance().getLogger().error("Cannot load fileitems '" + path.getFileName().toString() + "'");
                                    e.printStackTrace();
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void spawnBlockDrop(ItemLoot itemLoot, Player player, BlockSnapshot blockSnapshot) {
        if (!itemLoot.shouldLoot()) {
            return;
        }

        Sponge.getServer().getWorld(blockSnapshot.getWorldUniqueId()).ifPresent(world -> {
            Optional<FileInventoriesService> maybeService = Sponge.getServiceManager().provide(FileInventoriesService.class);
            if (!maybeService.isPresent()) {
                BoxOUtils.getInstance().getLogger().error("The FileInventoriesService cannot be found. Has FileInventories been installed on this server?");
                return;
            }

            Optional<ItemStack> maybeItem = maybeService.get().getItem(itemLoot.getId(), player);
            if (!maybeItem.isPresent()) {
                BoxOUtils.getInstance().getLogger().error("The FileItem for ID " + itemLoot.getId() + " cannot be found");
                return;
            }

            Entity entity = world.createEntity(EntityTypes.ITEM, blockSnapshot.getLocation().orElse(player.getLocation()).getPosition());
            ItemStack itemStack = maybeItem.get();
            int quantityToLoot = itemLoot.getQuantityToLoot();
            if (quantityToLoot > itemStack.getQuantity()) {
                itemStack.setQuantity(quantityToLoot);
            }

            entity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
            world.spawnEntity(entity);
        });
    }

    public void spawnMobDrop(ItemLoot itemLoot, Player player, Entity targetEntity) {
        if (!itemLoot.shouldLoot()) {
            return;
        }

        Optional<FileInventoriesService> maybeService = Sponge.getServiceManager().provide(FileInventoriesService.class);
        if (!maybeService.isPresent()) {
            BoxOUtils.getInstance().getLogger().warn("The FileInventoriesService cannot be found. Has FileInventories been installed on this server?");
            return;
        }

        Optional<ItemStack> maybeItem = maybeService.get().getItem(itemLoot.getId(), player);
        if (!maybeItem.isPresent()) {
            BoxOUtils.getInstance().getLogger().warn("The FileItem for ID " + itemLoot.getId() + " cannot be found");
            return;
        }

        Entity entity = targetEntity.getWorld().createEntity(EntityTypes.ITEM, targetEntity.getLocation().getPosition());

        ItemStack itemStack = maybeItem.get();
        int quantityToLoot = itemLoot.getQuantityToLoot();
        if (quantityToLoot > itemStack.getQuantity()) {
            itemStack.setQuantity(quantityToLoot);
        }

        entity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        targetEntity.getWorld().spawnEntity(entity);
    }
}
