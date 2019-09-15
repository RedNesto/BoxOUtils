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
import io.github.rednesto.bou.listeners.BlockSpawnersListener;
import io.github.rednesto.bou.listeners.CustomBlockDropsListener;
import io.github.rednesto.bou.listeners.CustomMobDropsListener;
import io.github.rednesto.bou.listeners.FastHarvestListener;
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
import java.util.Optional;
import java.util.function.Consumer;

public class SpongeConfig {

    private static final boolean IS_TESTING = BouUtils.isTesting();

    private static boolean fastHarvestListenersRegistered = false;
    private static boolean blockDropsListenersRegistered = false;
    private static boolean mobDropsListenersRegistered = false;
    private static boolean blockSpawnersListenersRegistered = false;

    public static void loadConf(BoxOUtils plugin) throws IOException {
        Files.createDirectories(plugin.getConfigDir());

        loadFastHarvest(plugin);
        loadCropsControl(plugin);
        loadBlocksDrops(plugin);
        loadMobsDrops(plugin);
        loadBlockSpawners(plugin);
    }

    private static void loadFastHarvest(BoxOUtils plugin) throws IOException {
        doLoad(plugin, "fastharvest.conf", "FastHarvest", BouTypeTokens.CONFIG_FAST_HARVEST, config -> {
            plugin.setFastHarvest(config);
            if (config.enabled && !fastHarvestListenersRegistered && !IS_TESTING) {
                Sponge.getEventManager().registerListeners(plugin, new FastHarvestListener());
                fastHarvestListenersRegistered = true;
            }
        });
    }

    private static void loadCropsControl(BoxOUtils plugin) throws IOException {
        doLoad(plugin, "cropscontrol.conf", "CropsControl", BouTypeTokens.CONFIG_CROPS_CONTROL, config -> {
            plugin.setCropsControl(config);
            if (config.enabled && !fastHarvestListenersRegistered && !IS_TESTING) {
                Sponge.getEventManager().registerListeners(plugin, new FastHarvestListener());
                fastHarvestListenersRegistered = true;
            }
        });
    }

    private static void loadBlocksDrops(BoxOUtils plugin) throws IOException {
        doLoad(plugin, "blocksdrops.conf", "BlocksDrops", BouTypeTokens.CONFIG_BLOCKS_DROPS, config -> {
            plugin.setBlocksDrops(config);
            if (config.enabled && !blockDropsListenersRegistered && !IS_TESTING) {
                Sponge.getEventManager().registerListeners(plugin, new CustomBlockDropsListener());
                blockDropsListenersRegistered = true;
            }
        });
    }

    private static void loadMobsDrops(BoxOUtils plugin) throws IOException {
        doLoad(plugin, "mobsdrops.conf", "MobsDrops", BouTypeTokens.CONFIG_MOBS_DROPS, config -> {
            plugin.setMobsDrops(config);
            if (config.enabled && !mobDropsListenersRegistered && !IS_TESTING) {
                Sponge.getEventManager().registerListeners(plugin, new CustomMobDropsListener());
                mobDropsListenersRegistered = true;
            }
        });
    }

    private static void loadBlockSpawners(BoxOUtils plugin) throws IOException {
        doLoad(plugin, "blockspawners.conf", "BlockSpawners", BouTypeTokens.CONFIG_BLOCK_SPAWNERS, config -> {
            plugin.setBlockSpawners(config);
            if (config.enabled && !blockSpawnersListenersRegistered && !IS_TESTING) {
                Sponge.getEventManager().registerListeners(plugin, new BlockSpawnersListener());
                blockSpawnersListenersRegistered = true;
            }
        });
    }

    private static <T> void doLoad(BoxOUtils plugin, String filename, String presentableConfigName, TypeToken<T> configToken, Consumer<T> loadCallback) throws IOException {
        ConfigurationNode rootNode = loadFileSafely(plugin, filename, presentableConfigName);

        T loadedConfig = null;
        try {
            loadedConfig = rootNode.getValue(configToken);
        } catch (ObjectMappingException e) {
            plugin.getLogger().error("Unable to read " + presentableConfigName + " configuration.", e);
        }

        if (loadedConfig == null) {
            return;
        }

        loadCallback.accept(loadedConfig);
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
                .registerType(BouTypeTokens.ITEM_LOOT, new ItemLootSerializer())
                .registerType(BouTypeTokens.CUSTOM_LOOT_REUSE, new CustomLootReuseSerializer())
                .registerType(BouTypeTokens.CUSTOM_LOOT_COMMAND, new CustomLootCommandSerializer())
                .registerType(BouTypeTokens.REQUIREMENT, new RequirementSerializer())
                .registerType(BouTypeTokens.REQUIREMENTS_MAP, new RequirementsMapSerializer())
                .registerType(BouTypeTokens.MONEY_LOOT, new MoneyLootSerializer())
                .registerType(BouTypeTokens.INT_QUANTITY, new IntQuantitySerializer())
                .registerType(BouTypeTokens.LOOT_REUSE, new LootReuseSerializer())
                // BlockSpawners
                .registerType(BouTypeTokens.SPAWNED_MOB, new SpawnedMobSerializer())
                // FastHarvest
                .registerType(BouTypeTokens.FAST_HARVEST_CROP, new FastHarvestCropSerializer())
                .registerType(BouTypeTokens.FAST_HARVEST_TOOLS, new FastHarvestToolsSerializer())
                // Configurations
                .registerType(BouTypeTokens.CONFIG_BLOCK_SPAWNERS, new ConfigBlockSpawnerSerializer());
    }
}
