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
package io.github.rednesto.bou;

import io.github.rednesto.bou.config.serializers.*;
import io.github.rednesto.bou.listeners.*;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SpongeConfig {

    private static final boolean IS_TESTING = BouUtils.isTesting();

    private static final List<Feature<?, ?>> FEATURES = Arrays.asList(
            new Feature<>("fastharvest.conf", "FastHarvest", BouTypeTokens.CONFIG_FAST_HARVEST, BoxOUtils::setFastHarvest, FastHarvestListener::new),
            new Feature<>("cropscontrol.conf", "CropsControl", BouTypeTokens.CONFIG_CROPS_CONTROL, BoxOUtils::setCropsControl),
            new Feature<>("blocksdrops.conf", "BlocksDrops", BouTypeTokens.CONFIG_BLOCKS_DROPS, BoxOUtils::setBlocksDrops, CustomBlockDropsListener::new),
            new Feature<>("fishingdrops.conf", "FishingDrops", BouTypeTokens.CONFIG_FISHING_DROPS, BoxOUtils::setFishingDrops, CustomFishingDropsListener::new),
            new Feature<>("mobsdrops.conf", "MobsDrops", BouTypeTokens.CONFIG_MOBS_DROPS, BoxOUtils::setMobsDrops, CustomMobDropsListener::new),
            new Feature<>("blockspawners.conf", "BlockSpawners", BouTypeTokens.CONFIG_BLOCK_SPAWNERS, BoxOUtils::setBlockSpawners, BlockSpawnersListener::new)
    );

    public static void loadConf(BoxOUtils plugin) throws IOException {
        Files.createDirectories(plugin.getConfigDir());
        for (Feature<?, ?> feature : FEATURES) {
            loadFeature(plugin, feature);
        }
    }

    private static <C, L> void loadFeature(BoxOUtils plugin, Feature<C, L> feature) throws IOException {
        ConfigurationNode rootNode = loadFileSafely(plugin, feature.filename, feature.presentableConfigName);

        try {
            @Nullable C loadedConfig = rootNode.get(feature.configToken);
            if (loadedConfig != null) {
                feature.configSetter.accept(plugin, loadedConfig);
            }

            @Nullable ListenerWrapper<L> listenerWrapper = feature.listener;
            if (listenerWrapper != null) {
                if (loadedConfig instanceof Config.ToggleableConfig) {
                    @Nullable L existingListener = listenerWrapper.listener;
                    if (((Config.ToggleableConfig) loadedConfig).isEnabled()) {
                        registerOrReloadListener(plugin, listenerWrapper);
                    } else if (existingListener != null) {
                        listenerWrapper.listener = null;
                        Sponge.eventManager().unregisterListeners(existingListener);
                    }
                } else {
                    registerOrReloadListener(plugin, listenerWrapper);
                }
            }
        } catch (SerializationException e) {
            plugin.getLogger().error("Unable to read " + feature.presentableConfigName + " configuration.", e);
        }
    }

    private static <L> void registerOrReloadListener(BoxOUtils plugin, ListenerWrapper<L> wrapper) {
        if (IS_TESTING) {
            return;
        }

        @Nullable L existingListener = wrapper.listener;
        if (existingListener == null) {
            L newListener = wrapper.listenerSupplier.get();
            Sponge.eventManager().registerListeners(plugin.getContainer(), newListener);
            wrapper.listener = newListener;
        } else if (existingListener instanceof ReloadableListener) {
            ((ReloadableListener) existingListener).reload();
        }
    }

    private static ConfigurationNode loadFileSafely(BoxOUtils plugin, String filename, String presentableConfigName) throws IOException {
        Path destFile = plugin.getConfigDir().resolve(filename);
        if (Files.notExists(destFile)) {
            @Nullable InputStream inputStream = null;
            try {
                inputStream = plugin.getContainer().openResource(new URI("assets/box-o-utils/config/" + filename)).orElse(null);
                if (inputStream != null) {
                    Files.copy(inputStream, destFile);
                }
            } catch (IOException | URISyntaxException e) {
                plugin.getLogger().error("Cannot get default " + presentableConfigName + " configuration file.", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        plugin.getLogger().error("Error when closing default config resource " + presentableConfigName + ".", e);
                    }
                }
            }
        }

        return loader(destFile).load();
    }

    public static HoconConfigurationLoader loader(Path path) {
        HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder()
                .path(path)
                .defaultOptions(ConfigurationOptions.defaults().serializers(createSerializersCollection()));
        // TODO see if we can add an includer in Configurate 4
        //try {
        //    // SpongeForge relocates the HOCON library to avoid conflicts with Forge, which uses an older version of it.
        //    // See https://github.com/SpongePowered/SpongeForge/blob/007b0f5734981fc050cb79f8409e36a5ad64bbdc/build.gradle#L155
        //    // It would be very hard, if not impossible, to work around this issue, so I just disable the includer on SpongeForge
        //    Class.forName("configurate.typesafe.config.ConfigParseOptions");
        //} catch (ClassNotFoundException e) {
        //    builder.setParseOptions(ConfigParseOptions.defaults().appendIncluder(new SimpleConfigIncluderFile(path.getParent())));
        //}

        return builder.build();
    }

    public static TypeSerializerCollection createSerializersCollection() {
        return populatePluginSerializers(TypeSerializerCollection.defaults().childBuilder()).build();
    }

    public static TypeSerializerCollection.Builder populatePluginSerializers(TypeSerializerCollection.Builder builder) {
        return builder
                // MobsDrops / BlocksDrops
                .register(BouTypeTokens.CUSTOM_LOOT, new CustomLootSerializer())
                .register(BouTypeTokens.CUSTOM_LOOT_RECIPIENT, new CustomLootRecipientSerializer())
                .register(BouTypeTokens.CUSTOM_DROPS_PROVIDER_LIST, new CustomDropsProviderListSerializer())
                .register(BouTypeTokens.CUSTOM_LOOT_REUSE, new CustomLootReuseSerializer())
                .register(BouTypeTokens.CUSTOM_LOOT_COMMAND, new CustomLootCommandSerializer())
                .register(BouTypeTokens.REQUIREMENT, new RequirementSerializer())
                .register(BouTypeTokens.REQUIREMENTS_MAP, new RequirementsMapSerializer())
                .register(BouTypeTokens.MONEY_LOOT, new MoneyLootSerializer())
                .register(BouTypeTokens.INT_QUANTITY, new IntQuantitySerializer())
                .register(BouTypeTokens.INT_RANGE, new IntRangeSerializer())
                .register(BouTypeTokens.LOOT_REUSE, new LootReuseSerializer())
                .register(BouTypeTokens.ENCHANTMENTS_FILTER, new EnchantmentsFilterSerializer())
                // BlockSpawners
                .register(BouTypeTokens.SPAWNED_MOB, new SpawnedMobSerializer())
                // FastHarvest
                .register(BouTypeTokens.FAST_HARVEST_CROP, new FastHarvestCropSerializer())
                .register(BouTypeTokens.FAST_HARVEST_TOOLS, new FastHarvestToolsSerializer())
                // Configurations
                .register(BouTypeTokens.CONFIG_BLOCK_SPAWNERS, new ConfigBlockSpawnerSerializer())
                // SpongeAPI types
                .register(ResourceKey.class, new ResourceKeySerializer());
    }

    private static class ListenerWrapper<L> {

        private final Supplier<L> listenerSupplier;
        @Nullable
        private L listener;

        private ListenerWrapper(Supplier<L> listenerSupplier) {
            this.listenerSupplier = listenerSupplier;
        }
    }

    private static class Feature<C, L> {

        private final String filename;
        private final String presentableConfigName;
        private final TypeToken<C> configToken;
        private final BiConsumer<BoxOUtils, C> configSetter;
        @Nullable
        private final ListenerWrapper<L> listener;

        private Feature(String filename, String presentableConfigName, TypeToken<C> configToken, BiConsumer<BoxOUtils, C> configSetter, @Nullable ListenerWrapper<L> listener) {
            this.filename = filename;
            this.presentableConfigName = presentableConfigName;
            this.configToken = configToken;
            this.configSetter = configSetter;
            this.listener = listener;
        }

        private Feature(String filename, String presentableConfigName, TypeToken<C> configToken, BiConsumer<BoxOUtils, C> configSetter, Supplier<L> listenerSupplier) {
            this(filename, presentableConfigName, configToken, configSetter, new ListenerWrapper<>(listenerSupplier));
        }

        private Feature(String filename, String presentableConfigName, TypeToken<C> configToken, BiConsumer<BoxOUtils, C> configSetter) {
            this(filename, presentableConfigName, configToken, configSetter, (ListenerWrapper<L>) null);
        }
    }

    public interface ReloadableListener {

        void reload();
    }
}
