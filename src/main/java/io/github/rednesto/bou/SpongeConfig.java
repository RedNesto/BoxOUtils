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

import com.google.common.reflect.TypeToken;
import com.typesafe.config.ConfigParseOptions;
import io.github.rednesto.bou.config.SimpleConfigIncluderFile;
import io.github.rednesto.bou.config.serializers.*;
import io.github.rednesto.bou.listeners.*;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

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
            C loadedConfig = rootNode.getValue(feature.configToken);
            if (loadedConfig != null) {
                feature.configSetter.accept(plugin, loadedConfig);
            }

            ListenerWrapper<L> listenerWrapper = feature.listener;
            if (listenerWrapper != null) {
                if (loadedConfig instanceof Config.ToggleableConfig) {
                    L existingListener = listenerWrapper.listener;
                    if (((Config.ToggleableConfig) loadedConfig).isEnabled()) {
                        registerOrReloadListener(plugin, listenerWrapper);
                    } else if (existingListener != null) {
                        listenerWrapper.listener = null;
                        Sponge.getEventManager().unregisterListeners(existingListener);
                    }
                } else {
                    registerOrReloadListener(plugin, listenerWrapper);
                }
            }
        } catch (ObjectMappingException e) {
            plugin.getLogger().error("Unable to read " + feature.presentableConfigName + " configuration.", e);
        }
    }

    private static <L> void registerOrReloadListener(BoxOUtils plugin, ListenerWrapper<L> wrapper) {
        if (IS_TESTING) {
            return;
        }

        L existingListener = wrapper.listener;
        if (existingListener == null) {
            L newListener = wrapper.listenerSupplier.get();
            Sponge.getEventManager().registerListeners(plugin, newListener);
            wrapper.listener = newListener;
        } else if (existingListener instanceof ReloadableListener) {
            ((ReloadableListener) existingListener).reload();
        }
    }

    private static ConfigurationNode loadFileSafely(BoxOUtils plugin, String filename, String presentableConfigName) throws IOException {
        Path destFile = plugin.getConfigDir().resolve(filename);
        if (Files.notExists(destFile)) {
            Optional<Asset> defaultConf = Sponge.getAssetManager().getAsset(plugin, "config/" + filename);
            if (defaultConf.isPresent()) {
                defaultConf.get().copyToFile(destFile);
            } else {
                plugin.getLogger().error("Cannot get default " + presentableConfigName + " configuration file.");
            }
        }

        return loader(destFile).load();
    }

    public static HoconConfigurationLoader loader(Path path) {
        HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder()
                .setPath(path)
                .setDefaultOptions(ConfigurationOptions.defaults().setSerializers(createSerializersCollection()));
        try {
            // SpongeForge relocates the HOCON library to avoid conflicts with Forge, which uses an older version of it.
            // See https://github.com/SpongePowered/SpongeForge/blob/007b0f5734981fc050cb79f8409e36a5ad64bbdc/build.gradle#L155
            // It would be very hard, if not impossible, to work around this issue, so I just disable the includer on SpongeForge
            Class.forName("configurate.typesafe.config.ConfigParseOptions");
        } catch (ClassNotFoundException e) {
            builder.setParseOptions(ConfigParseOptions.defaults().appendIncluder(new SimpleConfigIncluderFile(path.getParent())));
        }

        return builder.build();
    }

    public static TypeSerializerCollection createSerializersCollection() {
        return populatePluginSerializers(TypeSerializers.getDefaultSerializers().newChild());
    }

    public static TypeSerializerCollection populatePluginSerializers(TypeSerializerCollection collection) {
        return collection
                // MobsDrops / BlocksDrops
                .registerType(BouTypeTokens.CUSTOM_LOOT, new CustomLootSerializer())
                .registerType(BouTypeTokens.CUSTOM_LOOT_RECIPIENT, new CustomLootRecipientSerializer())
                .registerType(BouTypeTokens.CUSTOM_DROPS_PROVIDER_LIST, new CustomDropsProviderListSerializer())
                .registerType(BouTypeTokens.CUSTOM_LOOT_REUSE, new CustomLootReuseSerializer())
                .registerType(BouTypeTokens.CUSTOM_LOOT_COMMAND, new CustomLootCommandSerializer())
                .registerType(BouTypeTokens.REQUIREMENT, new RequirementSerializer())
                .registerType(BouTypeTokens.REQUIREMENTS_MAP, new RequirementsMapSerializer())
                .registerType(BouTypeTokens.MONEY_LOOT, new MoneyLootSerializer())
                .registerType(BouTypeTokens.INT_QUANTITY, new IntQuantitySerializer())
                .registerType(BouTypeTokens.INT_RANGE, new IntRangeSerializer())
                .registerType(BouTypeTokens.LOOT_REUSE, new LootReuseSerializer())
                .registerType(BouTypeTokens.ENCHANTMENTS_FILTER, new EnchantmentsFilterSerializer())
                // BlockSpawners
                .registerType(BouTypeTokens.SPAWNED_MOB, new SpawnedMobSerializer())
                // FastHarvest
                .registerType(BouTypeTokens.FAST_HARVEST_CROP, new FastHarvestCropSerializer())
                .registerType(BouTypeTokens.FAST_HARVEST_TOOLS, new FastHarvestToolsSerializer())
                // Configurations
                .registerType(BouTypeTokens.CONFIG_BLOCK_SPAWNERS, new ConfigBlockSpawnerSerializer());
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
