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

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.sponge.config.serializers.*;
import io.github.rednesto.bou.sponge.listeners.BlockSpawnersListener;
import io.github.rednesto.bou.sponge.listeners.CustomBlockDropsListener;
import io.github.rednesto.bou.sponge.listeners.CustomMobDropsListener;
import io.github.rednesto.bou.sponge.listeners.FastHarvestListener;
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

    private static boolean fastHarvestListenersRegistered = false;
    private static boolean blockDropsListenersRegistered = false;
    private static boolean mobDropsListenersRegistered = false;
    private static boolean blockSpawnersListenersRegistered = false;

    public static void loadConf(BoxOUtils plugin) throws IOException {
        Files.createDirectories(plugin.getConfigDir());

        loadFastHarvest(plugin);
        loadBlocksDrops(plugin);
        loadMobsDrops(plugin);
        loadBlockSpawners(plugin);

        IntegrationsManager.INSTANCE.initIntegrations(plugin);
    }

    private static void loadFastHarvest(BoxOUtils plugin) throws IOException {
        doLoad(plugin, "fastharvest.conf", "FastHarvest", BouTypeTokens.CONFIG_FAST_HARVEST, config -> {
            BoxOUtils.getInstance().setFastHarvest(config);
            if (config.enabled && !fastHarvestListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new FastHarvestListener());
                fastHarvestListenersRegistered = true;
            }
        });
    }

    private static void loadBlocksDrops(BoxOUtils plugin) throws IOException {
        doLoad(plugin, "blocksdrops.conf", "BlocksDrops", BouTypeTokens.CONFIG_BLOCKS_DROPS, config -> {
            BoxOUtils.getInstance().setBlocksDrops(config);
            if (config.enabled && !blockDropsListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new CustomBlockDropsListener());
                blockDropsListenersRegistered = true;
            }
        });
    }

    private static void loadMobsDrops(BoxOUtils plugin) throws IOException {
        doLoad(plugin, "mobsdrops.conf", "MobsDrops", BouTypeTokens.CONFIG_MOBS_DROPS, config -> {
            BoxOUtils.getInstance().setMobsDrops(config);
            if (config.enabled && !mobDropsListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new CustomMobDropsListener());
                mobDropsListenersRegistered = true;
            }
        });
    }

    private static void loadBlockSpawners(BoxOUtils plugin) throws IOException {
        doLoad(plugin, "blockspawners.conf", "BlockSpawners", BouTypeTokens.CONFIG_BLOCK_SPAWNERS, config -> {
            BoxOUtils.getInstance().setBlockSpawners(config);
            if (config.enabled && !blockSpawnersListenersRegistered) {
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

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setPath(destFile)
                .build();
        TypeSerializerCollection serializers = populatePluginSerializers(TypeSerializers.getDefaultSerializers().newChild());
        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);
        return loader.load(options);
    }

    public static TypeSerializerCollection populatePluginSerializers(TypeSerializerCollection collection) {
        return collection
                // MobsDrops / BlocksDrops
                .registerType(BouTypeTokens.CUSTOM_LOOT, new CustomLootSerializer())
                .registerType(BouTypeTokens.ITEM_LOOT, new ItemLootSerializer())
                .registerType(BouTypeTokens.CUSTOM_LOOT_REUSE, new CustomLootReuseSerializer())
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
