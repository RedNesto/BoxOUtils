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
package io.github.rednesto.bou.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.api.customdrops.CustomDropsProvider;
import io.github.rednesto.bou.api.customdrops.CustomDropsProviderFactory;
import io.github.rednesto.bou.api.customdrops.CustomDropsProviderFactoryIntegrations;
import io.github.rednesto.bou.api.customdrops.ProviderConfigurationException;
import io.github.rednesto.bou.integration.vanilla.VanillaCustomDropsProvider;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomDropsProviderListSerializer extends LintingTypeSerializer<List<CustomDropsProvider>> {

    @Nullable
    @Override
    public List<CustomDropsProvider> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) {
        List<@NonNull CustomDropsProvider> providers = new ArrayList<>();
        if (value.getValueType() == ValueType.SCALAR) {
            configureProvider(providers, value, VanillaCustomDropsProvider.Factory.ID);
        } else if (value.hasListChildren()) {
            for (ConfigurationNode dropNode : value.getChildrenList()) {
                if (dropNode.hasListChildren()) {
                    error(value, "Drop values cannot be lists");
                } else if (dropNode.hasMapChildren()) {
                    String providerId = dropNode.getNode("provider").getString(VanillaCustomDropsProvider.Factory.ID);
                    configureProvider(providers, dropNode, providerId);
                } else {
                    configureProvider(providers, dropNode, VanillaCustomDropsProvider.Factory.ID);
                }
            }
        } else if (value.hasMapChildren()) {
            if (!value.getNode("type").isVirtual()) {
                // The value can be an object configuring a single provider
                // 'type' is unlikely to be used as an ID (even in short form), so we assume this is the expected behaviour
                String providerId = value.getNode("provider").getString(VanillaCustomDropsProvider.Factory.ID);
                configureProvider(providers, value, providerId);
            } else {
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : value.getChildrenMap().entrySet()) {
                    Object key = entry.getKey();
                    if (!(key instanceof String)) {
                        error(entry.getValue(), "Drops keys must be non-null strings (got " + key + " of type " + key.getClass().getTypeName() + ").");
                    } else {
                        String providerId = ((String) key);

                        ConfigurationNode providerRootNode = entry.getValue();
                        if (providerRootNode.hasListChildren()){
                            for (ConfigurationNode dropNode : providerRootNode.getChildrenList()) {
                                configureProvider(providers, dropNode, providerId);
                            }
                        } else {
                            configureProvider(providers, providerRootNode, providerId);
                        }
                    }
                }
            }
        }
        return providers;
    }

    private void configureProvider(Collection<@NonNull CustomDropsProvider> providers, ConfigurationNode dropNode, String providerId) {
        CustomDropsProviderFactory factory = CustomDropsProviderFactoryIntegrations.getInstance().getById(providerId, true);
        if (factory == null) {
            error(dropNode, "Could not find CustomDropsProviderFactory for ID '" + providerId + "'");
            return;
        }

        try {
            CustomDropsProvider provider = factory.provide(dropNode);
            if (provider == null) {
                error(dropNode, "Provider '" + providerId + "' returned a null CustomDropsProvider. This should never happen, please contact the extension developer.");
                return;
            }
            providers.add(provider);
        } catch (ProviderConfigurationException e) {
            error(dropNode, "CustomDropsProvider ('" + providerId + "') error: " + e.getMessage());
        } catch (Throwable t) {
            error(dropNode, "Unhandled error when configuring CustomDropsProvider '" + providerId + "': " + t.getMessage());
        }
    }
}
