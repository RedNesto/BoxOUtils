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
package io.github.rednesto.bou.sponge;

import io.github.rednesto.bou.sponge.integration.ByteItemsCustomDropsProvider;
import io.github.rednesto.bou.sponge.integration.FileInventoriesCustomDropsProvider;
import io.github.rednesto.bou.sponge.integration.vanilla.VanillaCustomDropsProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public final class IntegrationsManager {

    public static final IntegrationsManager INSTANCE = new IntegrationsManager();

    private boolean customDropsProvidersInit = false;
    private final ICustomDropsProvider defaultCustomDropsProvider = new VanillaCustomDropsProvider();
    private final Map<String, ICustomDropsProvider> customDropsProviders = new HashMap<>();

    private IntegrationsManager() {}

    public Optional<ItemStack> createCustomDropStack(@Nullable String providerId, String id, @Nullable Player targetPlayer) {
        if (providerId == null)
            return defaultCustomDropsProvider.createItemStack(id, targetPlayer);

        ICustomDropsProvider provider = customDropsProviders.get(providerId);
        if (provider != null)
            return provider.createItemStack(id, targetPlayer);

        return Optional.empty();
    }

    void initCustomDropsProviders(BoxOUtils plugin) {
        if (customDropsProvidersInit)
            return;

        customDropsProvidersInit = true;
        defaultCustomDropsProvider.init(plugin);
        for (ICustomDropsProvider provider : customDropsProviders.values()) {
            provider.init(plugin);
        }
    }

    void loadIntegrations() {
        if (Sponge.getPluginManager().isLoaded("file-inventories")) {
            customDropsProviders.put("file-inv", new FileInventoriesCustomDropsProvider());
        }

        if (Sponge.getPluginManager().isLoaded("byte-items")) {
            customDropsProviders.put("byte-items", new ByteItemsCustomDropsProvider());
        }
    }
}
