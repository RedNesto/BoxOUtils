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
package io.github.rednesto.bou.sponge.integration;

import io.github.rednesto.bou.sponge.BoxOUtils;
import io.github.rednesto.bou.sponge.ICustomDropsProvider;
import io.github.rednesto.fileinventories.api.FileInventoriesService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

public class FileInventoriesCustomDropsProvider implements ICustomDropsProvider {

    @Override
    public void init(BoxOUtils plugin) {
        FileInventoriesService fileInvService = Sponge.getServiceManager().provide(FileInventoriesService.class).orElse(null);
        if (fileInvService == null) {
            plugin.getLogger().error("FileInventoriesService is not available but should be. CustomDrops using FileItems will not work.");
            return;
        }

        Path fileitems = plugin.getConfigDir().resolve("fileitems");
        if (!Files.isDirectory(fileitems))
            return;

        try (Stream<Path> files = Files.walk(fileitems)) {
            files.filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            fileInvService.load(FileInventoriesService.LoadTarget.ITEMS, path);
                        } catch (IOException e) {
                            plugin.getLogger().error("Could not load fileitems '" + path.getFileName().toString() + "'.", e);
                        }
                    });
        } catch (IOException e) {
            plugin.getLogger().error("Unable to walk in directory " + fileitems.toAbsolutePath(), e);
        }
    }

    @Override
    public Optional<ItemStack> createItemStack(String id, @Nullable Player targetPlayer) {
        Optional<FileInventoriesService> maybeService = Sponge.getServiceManager().provide(FileInventoriesService.class);
        if (!maybeService.isPresent()) {
            BoxOUtils.getInstance().getLogger().error("The FileInventoriesService cannot be found. Has FileInventories been installed on this server?");
            return Optional.empty();
        }

        Optional<ItemStack> item = maybeService.get().getItem(id, targetPlayer);
        if (!item.isPresent())
            BoxOUtils.getInstance().getLogger().error("The FileItem for ID " + id + " cannot be found");

        return item;
    }
}
