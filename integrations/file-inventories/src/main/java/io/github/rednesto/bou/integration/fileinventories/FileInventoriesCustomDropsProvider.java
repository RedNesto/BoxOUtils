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
package io.github.rednesto.bou.integration.fileinventories;

import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.api.customdrops.BasicCustomDropsProvider;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import io.github.rednesto.fileinventories.api.FileInventoriesService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class FileInventoriesCustomDropsProvider extends BasicCustomDropsProvider {

    public FileInventoriesCustomDropsProvider(String itemId,
                                                 @Nullable String displayname,
                                                 double chance,
                                                 @Nullable IntQuantity quantity) {
        super(itemId, displayname, chance, quantity);
    }

    @Nullable
    @Override
    protected ItemStack createStack(CustomLootProcessingContext context, ResourceKey itemId) {
        Optional<FileInventoriesService> maybeService = Sponge.getServiceManager().provide(FileInventoriesService.class);
        if (!maybeService.isPresent()) {
            BoxOUtils.getInstance().getLogger().error("The FileInventoriesService cannot be found. Has FileInventories been installed on this server?");
            return null;
        }

        Optional<ItemStack> item = maybeService.get().getItem(itemId, context.getTargetPlayer());
        if (!item.isPresent()) {
            BoxOUtils.getInstance().getLogger().error("The FileItem for ID " + itemId + " cannot be found");
        }

        return item.orElse(null);
    }

    public static class Factory extends BasicFactory {

        @Override
        protected BasicCustomDropsProvider provide(@Nullable ConfigurationNode node,
                                                   String itemId,
                                                   @Nullable String displayname,
                                                   double chance,
                                                   @Nullable IntQuantity quantity) {
            return new FileInventoriesCustomDropsProvider(itemId, displayname, chance, quantity);
        }

        @Override
        public void load(BoxOUtils plugin) {
            FileInventoriesService fileInvService = Sponge.getServiceManager().provide(FileInventoriesService.class).orElse(null);
            if (fileInvService == null) {
                plugin.getLogger().error("FileInventoriesService is not available. CustomDrops using FileItems will be ignored.");
                return;
            }

            Path fileitems = plugin.getConfigDir().resolve("fileitems");
            if (!Files.isDirectory(fileitems)) {
                return;
            }

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
        public String getId() {
            return "box-o-utils:file-inv";
        }
    }
}
