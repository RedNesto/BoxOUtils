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
package io.github.rednesto.bou.api.customdrops;

import io.github.rednesto.bou.IntegrationsManager;
import io.github.rednesto.bou.api.integration.IntegrationsList;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

import javax.annotation.Nullable;

public class CustomDropsProviderIntegrations extends IntegrationsList<CustomDropsProvider> {

    private final CustomDropsProvider vanillaProvider;

    public CustomDropsProviderIntegrations(CustomDropsProvider vanillaProvider) {
        super("CustomDropsProvider Integrations", "box-o-utils");
        this.vanillaProvider = vanillaProvider;
        register(vanillaProvider, true);
    }

    public Optional<ItemStack> createCustomDropStack(@Nullable String providerId, String id, @Nullable Player targetPlayer) {
        if (providerId == null) {
            return vanillaProvider.createItemStack(id, targetPlayer);
        }

        CustomDropsProvider provider = getById(providerId, true);
        if (provider != null) {
            return provider.createItemStack(id, targetPlayer);
        }

        return Optional.empty();
    }

    public CustomDropsProvider getDefaultIntegration() {
        return this.vanillaProvider;
    }

    public static CustomDropsProviderIntegrations getInstance() {
        return IntegrationsManager.getInstance().getCustomDropsProviderIntegrations();
    }
}
