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

import io.github.rednesto.bou.common.BoundedIntQuantity;
import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.CustomLoot;
import io.github.rednesto.bou.common.ItemLoot;
import io.github.rednesto.bou.common.MoneyLoot;
import io.github.rednesto.bou.common.SpawnedMob;
import io.github.rednesto.bou.sponge.listeners.BlockSpawnersListener;
import io.github.rednesto.bou.sponge.listeners.CustomBlockDropsListener;
import io.github.rednesto.bou.sponge.listeners.CustomMobDropsListener;
import io.github.rednesto.bou.sponge.listeners.FastHarvestListener;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.TypeTokens;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.rednesto.bou.common.Config.*;

public class SpongeConfig {

    private static boolean fastHarvestListenersRegistered = false;
    private static boolean blockDropsListenersRegistered = false;
    private static boolean mobDropsListenersRegistered = false;
    private static boolean blockSpawnersListenersRegistered = false;

    public static void loadConf(BoxOUtils plugin) throws IOException {
        Files.createDirectories(plugin.getConfigDir());

        File fastHarvestConfFile = new File(plugin.getConfigDir().toFile(), "fastharvest.conf");

        if (!fastHarvestConfFile.exists())
            Files.copy(plugin.getClass().getResourceAsStream("/fastharvest.conf"), fastHarvestConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ConfigurationNode fastHarvestConf = HoconConfigurationLoader.builder().setFile(fastHarvestConfFile).build().load();

        if (fastHarvestConf.getNode("enabled").getBoolean(false)) {
            if (!fastHarvestListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new FastHarvestListener());
                fastHarvestListenersRegistered = true;
            }

            SEED_DROP_MINIMUM = fastHarvestConf.getNode("seed", "minimum").getInt();
            SEED_DROP_COUNT = fastHarvestConf.getNode("seed", "count").getInt();
            SEED_DROP_FORTUNE_FACTOR = fastHarvestConf.getNode("seed", "fortune_factor").getInt();
            SEED_DROP_CHANCE = fastHarvestConf.getNode("seed", "chance").getInt();
            SEED_DROP_CHANCE_OF = fastHarvestConf.getNode("seed", "chance_of").getInt();

            WHEAT_DROP_MINIMUM = fastHarvestConf.getNode("wheat", "minimum").getInt();
            WHEAT_DROP_COUNT = fastHarvestConf.getNode("wheat", "count").getInt();
            WHEAT_DROP_FORTUNE_FACTOR = fastHarvestConf.getNode("wheat", "fortune_factor").getInt();
            WHEAT_DROP_CHANCE = fastHarvestConf.getNode("wheat", "chance").getInt();
            WHEAT_DROP_CHANCE_OF = fastHarvestConf.getNode("wheat", "chance_of").getInt();

            CARROT_DROP_MINIMUM = fastHarvestConf.getNode("carrot", "minimum").getInt();
            CARROT_DROP_COUNT = fastHarvestConf.getNode("carrot", "count").getInt();
            CARROT_DROP_FORTUNE_FACTOR = fastHarvestConf.getNode("carrot", "fortune_factor").getInt();
            CARROT_DROP_CHANCE = fastHarvestConf.getNode("carrot", "chance").getInt();
            CARROT_DROP_CHANCE_OF = fastHarvestConf.getNode("carrot", "chance_of").getInt();

            POTATO_DROP_MINIMUM = fastHarvestConf.getNode("potato", "minimum").getInt();
            POTATO_DROP_COUNT = fastHarvestConf.getNode("potato", "count").getInt();
            POTATO_DROP_FORTUNE_FACTOR = fastHarvestConf.getNode("potato", "fortune_factor").getInt();
            POTATO_DROP_CHANCE = fastHarvestConf.getNode("potato", "chance").getInt();
            POTATO_DROP_CHANCE_OF = fastHarvestConf.getNode("potato", "chance_of").getInt();

            BEETROOT_SEED_DROP_MINIMUM = fastHarvestConf.getNode("beetroot_seed", "minimum").getInt();
            BEETROOT_SEED_DROP_COUNT = fastHarvestConf.getNode("beetroot_seed", "count").getInt();
            BEETROOT_SEED_DROP_FORTUNE_FACTOR = fastHarvestConf.getNode("beetroot_seed", "fortune_factor").getInt();
            BEETROOT_SEED_DROP_CHANCE = fastHarvestConf.getNode("beetroot_seed", "chance").getInt();
            BEETROOT_SEED_DROP_CHANCE_OF = fastHarvestConf.getNode("beetroot_seed", "chance_of").getInt();

            BEETROOT_DROP_MINIMUM = fastHarvestConf.getNode("beetroot", "minimum").getInt();
            BEETROOT_DROP_COUNT = fastHarvestConf.getNode("beetroot", "count").getInt();
            BEETROOT_DROP_FORTUNE_FACTOR = fastHarvestConf.getNode("beetroot", "fortune_factor").getInt();
            BEETROOT_DROP_CHANCE = fastHarvestConf.getNode("beetroot", "chance").getInt();
            BEETROOT_DROP_CHANCE_OF = fastHarvestConf.getNode("beetroot", "chance_of").getInt();

            HARVEST_LIST_ENABLED = fastHarvestConf.getNode("list", "enabled").getBoolean(false);
            HARVEST_TOOLS.clear();
            if (HARVEST_LIST_ENABLED) {
                HARVEST_LIST_IS_WHITELIST = fastHarvestConf.getNode("list", "is_whitelist").getBoolean(true);
                try {
                    HARVEST_TOOLS = fastHarvestConf.getNode("list", "tools").getList(TypeTokens.STRING_TOKEN);
                } catch (ObjectMappingException e) {
                    plugin.getLogger().error("An error occurred while reading the list of tools for FastHarvest");
                    e.printStackTrace();
                }
            }
        }

        File blocksDropsConfFile = new File(plugin.getConfigDir().toFile(), "blocksdrops.conf");

        if (!blocksDropsConfFile.exists())
            Files.copy(plugin.getClass().getResourceAsStream("/blocksdrops.conf"), blocksDropsConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ConfigurationNode blocksDropsConf = HoconConfigurationLoader.builder().setFile(blocksDropsConfFile).build().load();

        Config.CUSTOM_BLOCKS_DROPS_ENABLED = blocksDropsConf.getNode("enabled").getBoolean(false);
        Config.CUSTOM_BLOCKS_DROPS.clear();
        if (Config.CUSTOM_BLOCKS_DROPS_ENABLED) {
            if (!blockDropsListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new CustomBlockDropsListener());
                blockDropsListenersRegistered = true;
            }

            for (Map.Entry<Object, ? extends ConfigurationNode> child : blocksDropsConf.getNode("blocks").getChildrenMap().entrySet()) {
                List<ItemLoot> itemLoots = new ArrayList<>();
                readDrops(plugin, child, itemLoots);
                ConfigurationNode node = child.getValue();
                Config.CUSTOM_BLOCKS_DROPS.put((String) child.getKey(),
                        new CustomLoot(itemLoots, node.getNode("experience").getInt(), node.getNode("overwrite").getBoolean(false),
                                node.getNode("exp-overwrite").getBoolean(false), readMoneyLoot(node.getNode("money"))));
            }
        }

        File mobsDropsConfFile = new File(plugin.getConfigDir().toFile(), "mobsdrops.conf");

        if (!mobsDropsConfFile.exists())
            Files.copy(plugin.getClass().getResourceAsStream("/mobsdrops.conf"), mobsDropsConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ConfigurationNode mobsDropsConf = HoconConfigurationLoader.builder().setFile(mobsDropsConfFile).build().load();

        Config.CUSTOM_MOBS_DROPS_ENABLED = mobsDropsConf.getNode("enabled").getBoolean(false);
        Config.CUSTOM_MOBS_DROPS.clear();
        if (Config.CUSTOM_MOBS_DROPS_ENABLED) {
            if (!mobDropsListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new CustomMobDropsListener());
                mobDropsListenersRegistered = true;
            }

            for (Map.Entry<Object, ? extends ConfigurationNode> child : mobsDropsConf.getNode("mobs").getChildrenMap().entrySet()) {
                List<ItemLoot> itemLoots = new ArrayList<>();
                readDrops(plugin, child, itemLoots);
                ConfigurationNode node = child.getValue();

                Config.CUSTOM_MOBS_DROPS.put((String) child.getKey(), new CustomLoot(itemLoots, node.getNode("experience").getInt(),
                        node.getNode("overwrite").getBoolean(false), node.getNode("exp-overwrite").getBoolean(false), readMoneyLoot(node.getNode("money"))));
            }
        }

        File blockSpawnersConfFile = new File(plugin.getConfigDir().toFile(), "blockspawners.conf");

        if (!blockSpawnersConfFile.exists())
            Files.copy(plugin.getClass().getResourceAsStream("/blockspawners.conf"), blockSpawnersConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ConfigurationNode blockSpawnersConf = HoconConfigurationLoader.builder().setFile(blockSpawnersConfFile).build().load();

        Config.BLOCK_SPAWNERS_ENABLED = blockSpawnersConf.getNode("enabled").getBoolean(false);
        if (Config.BLOCK_SPAWNERS_ENABLED) {
            if (!blockSpawnersListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new BlockSpawnersListener());
                blockSpawnersListenersRegistered = true;
            }

            Config.BLOCK_SPAWNERS_DROPS.clear();
            for (Map.Entry<Object, ? extends ConfigurationNode> child : blockSpawnersConf.getNode("blocks").getChildrenMap().entrySet()) {
                ConfigurationNode node = child.getValue();
                List<SpawnedMob> spawnedMobs = node.getNode("spawns").getChildrenList().stream()
                        .map(spawn -> {
                            String[] quantityBounds = spawn.getNode("quantity").getString("1-1").split("-");
                            return new SpawnedMob(spawn.getNode("type").getString(), spawn.getNode("chance").getInt(), Integer.parseInt(quantityBounds[0]), Integer.parseInt(quantityBounds[1]));
                        })
                        .collect(Collectors.toList());
                Config.BLOCK_SPAWNERS_DROPS.put((String) child.getKey(), spawnedMobs);
            }
        }

        if (Config.CUSTOM_BLOCKS_DROPS_ENABLED || Config.CUSTOM_MOBS_DROPS_ENABLED)
            IntegrationsManager.INSTANCE.initCustomDropsProviders(plugin);
    }

    public static void readDrops(BoxOUtils plugin, Map.Entry<Object, ? extends ConfigurationNode> child, List<ItemLoot> itemLoots) {
        child.getValue().getNode("drops").getChildrenList().forEach(customLoot -> {
            String providerId;
            String itemId;
            if (customLoot.getNode("file_inv_id").isVirtual()) {
                providerId = customLoot.getNode("provider").getString();
                itemId = customLoot.getNode("type").getString();
            } else {
                // TODO Remove this branch in a future update. Only exists for backwards compatibility
                providerId = "file-inv";
                itemId = customLoot.getNode("file_inv_id").getString();
                plugin.getLogger().warn("The CustomDrop for '" + child.getKey() + "' uses the 'file_inv_id' property which will be removed in a future version.");
                plugin.getLogger().warn("Please replace this key with 'type' and add 'provider = \"file-inv\"' beside it.");
            }

            if (itemId == null) {
                plugin.getLogger().warn("The CustomDrop for '" + child.getKey() + "' does not have a 'type'. It will not be loaded.");
                return;
            }

            String[] quantityBounds = customLoot.getNode("quantity").getString("1-1").split("-");
            itemLoots.add(new ItemLoot(itemId, providerId, customLoot.getNode("displayname").getString(), customLoot.getNode("chance").getInt(), Integer.parseInt(quantityBounds[0]),
                    Integer.parseInt(quantityBounds[1])));
        });
    }

    public static MoneyLoot readMoneyLoot(ConfigurationNode moneyNode) {
        String amount = moneyNode.getNode("amount").getString();
        if (amount == null) {
            return null;
        }

        return new MoneyLoot(BoundedIntQuantity.parse(amount), moneyNode.getNode("currency").getString(), moneyNode.getNode("chance").getInt(), moneyNode.getNode("message").getString());
    }
}
