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

import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.api.customdrops.CustomDropsProvider;
import io.github.rednesto.bou.api.customdrops.CustomDropsProviderFactory;
import io.github.rednesto.bou.api.customdrops.CustomDropsProviderFactoryIntegrations;
import io.github.rednesto.bou.api.customdrops.ProviderConfigurationException;
import io.github.rednesto.bou.integration.vanilla.VanillaCustomDropsProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomDropsProviderListSerializer implements TypeSerializer<List<CustomDropsProvider>> {

    @Nullable
    @Override
    public List<CustomDropsProvider> deserialize(Type type, ConfigurationNode value) {
        List<@NonNull CustomDropsProvider> providers = new ArrayList<>();
        if (value.rawScalar() != null) {
            configureProvider(providers, value, VanillaCustomDropsProvider.Factory.ID);
        } else if (value.isList()) {
            for (ConfigurationNode dropNode : value.childrenList()) {
                if (dropNode.isList()) {
                    BoxOUtils.getInstance().getLogger().error("Drop values cannot be lists, they will be ignored.");
                } else if (dropNode.isMap()) {
                    String providerId = dropNode.node("provider").getString(VanillaCustomDropsProvider.Factory.ID);
                    configureProvider(providers, dropNode, providerId);
                } else {
                    configureProvider(providers, dropNode, VanillaCustomDropsProvider.Factory.ID);
                }
            }
        } else if (value.isMap()) {
            if (!value.node("type").virtual()) {
                // The value can be an object configuring a single provider
                // 'type' is unlikely to be used as an ID (even in short form), so we assume this is the expected behaviour
                String providerId = value.node("provider").getString(VanillaCustomDropsProvider.Factory.ID);
                configureProvider(providers, value, providerId);
            } else {
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : value.childrenMap().entrySet()) {
                    Object key = entry.getKey();
                    if (!(key instanceof String)) {
                        BoxOUtils.getInstance().getLogger().error("Drops keys must be non-null strings (got {}).", key);
                    } else {
                        String providerId = ((String) key);

                        ConfigurationNode providerRootNode = entry.getValue();
                        if (providerRootNode.isList()){
                            for (ConfigurationNode dropNode : providerRootNode.childrenList()) {
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

    @Override
    public void serialize(Type type, @Nullable List<CustomDropsProvider> obj, ConfigurationNode value) throws SerializationException {
    }

    private static void configureProvider(Collection<@NonNull CustomDropsProvider> providers, ConfigurationNode dropNode, String providerId) {
        @Nullable CustomDropsProviderFactory factory = CustomDropsProviderFactoryIntegrations.getInstance().getById(providerId, true);
        if (factory == null) {
            BoxOUtils.getInstance().getLogger().error("Could not find CustomDropsProviderFactory for ID '{}', it will be ignored.", providerId);
            return;
        }

        try {
            CustomDropsProvider provider = factory.provide(dropNode);
            if (provider == null) {
                BoxOUtils.getInstance().getLogger().error("Provider '{}' returned a null CustomDropsProvider, it will be ignored.", providerId);
                return;
            }
            providers.add(provider);
        } catch (ProviderConfigurationException e) {
            BoxOUtils.getInstance().getLogger().error("Failed to configure a CustomDropsProvider, it will be ignored.", e);
        } catch (Throwable t) {
            BoxOUtils.getInstance().getLogger().error("Unhandled error when configuring a CustomDropsProvider, it will be ignored.", t);
        }
    }
}
