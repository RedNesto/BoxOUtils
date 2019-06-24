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

import io.github.rednesto.bou.common.*;
import io.github.rednesto.bou.common.lootReuse.LootReuse;
import io.github.rednesto.bou.common.lootReuse.MultiplyLootReuse;
import io.github.rednesto.bou.common.lootReuse.SimpleLootReuse;
import io.github.rednesto.bou.common.quantity.BoundedIntQuantity;
import io.github.rednesto.bou.common.quantity.FixedIntQuantity;
import io.github.rednesto.bou.common.quantity.IIntQuantity;
import io.github.rednesto.bou.common.requirement.CustomLootRequirement;
import io.github.rednesto.bou.common.requirement.CustomLootRequirementProvider;
import io.github.rednesto.bou.common.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.sponge.listeners.BlockSpawnersListener;
import io.github.rednesto.bou.sponge.listeners.CustomBlockDropsListener;
import io.github.rednesto.bou.sponge.listeners.CustomMobDropsListener;
import io.github.rednesto.bou.sponge.listeners.FastHarvestListener;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.util.TypeTokens;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.annotation.Nullable;

import static io.github.rednesto.bou.common.Config.*;

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
        ConfigurationNode rootNode = loadFileSafely(plugin, "fastharvest.conf", "FastHarvest");

        FAST_HARVEST_ENABLED = rootNode.getNode("enabled").getBoolean(false);
        if (FAST_HARVEST_ENABLED) {
            if (!fastHarvestListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new FastHarvestListener());
                fastHarvestListenersRegistered = true;
            }

            SEED_DROP_MINIMUM = rootNode.getNode("seed", "minimum").getInt();
            SEED_DROP_COUNT = rootNode.getNode("seed", "count").getInt();
            SEED_DROP_FORTUNE_FACTOR = rootNode.getNode("seed", "fortune_factor").getInt();
            SEED_DROP_CHANCE = rootNode.getNode("seed", "chance").getInt();
            SEED_DROP_CHANCE_OF = rootNode.getNode("seed", "chance_of").getInt();

            WHEAT_DROP_MINIMUM = rootNode.getNode("wheat", "minimum").getInt();
            WHEAT_DROP_COUNT = rootNode.getNode("wheat", "count").getInt();
            WHEAT_DROP_FORTUNE_FACTOR = rootNode.getNode("wheat", "fortune_factor").getInt();
            WHEAT_DROP_CHANCE = rootNode.getNode("wheat", "chance").getInt();
            WHEAT_DROP_CHANCE_OF = rootNode.getNode("wheat", "chance_of").getInt();

            CARROT_DROP_MINIMUM = rootNode.getNode("carrot", "minimum").getInt();
            CARROT_DROP_COUNT = rootNode.getNode("carrot", "count").getInt();
            CARROT_DROP_FORTUNE_FACTOR = rootNode.getNode("carrot", "fortune_factor").getInt();
            CARROT_DROP_CHANCE = rootNode.getNode("carrot", "chance").getInt();
            CARROT_DROP_CHANCE_OF = rootNode.getNode("carrot", "chance_of").getInt();

            POTATO_DROP_MINIMUM = rootNode.getNode("potato", "minimum").getInt();
            POTATO_DROP_COUNT = rootNode.getNode("potato", "count").getInt();
            POTATO_DROP_FORTUNE_FACTOR = rootNode.getNode("potato", "fortune_factor").getInt();
            POTATO_DROP_CHANCE = rootNode.getNode("potato", "chance").getInt();
            POTATO_DROP_CHANCE_OF = rootNode.getNode("potato", "chance_of").getInt();

            BEETROOT_SEED_DROP_MINIMUM = rootNode.getNode("beetroot_seed", "minimum").getInt();
            BEETROOT_SEED_DROP_COUNT = rootNode.getNode("beetroot_seed", "count").getInt();
            BEETROOT_SEED_DROP_FORTUNE_FACTOR = rootNode.getNode("beetroot_seed", "fortune_factor").getInt();
            BEETROOT_SEED_DROP_CHANCE = rootNode.getNode("beetroot_seed", "chance").getInt();
            BEETROOT_SEED_DROP_CHANCE_OF = rootNode.getNode("beetroot_seed", "chance_of").getInt();

            BEETROOT_DROP_MINIMUM = rootNode.getNode("beetroot", "minimum").getInt();
            BEETROOT_DROP_COUNT = rootNode.getNode("beetroot", "count").getInt();
            BEETROOT_DROP_FORTUNE_FACTOR = rootNode.getNode("beetroot", "fortune_factor").getInt();
            BEETROOT_DROP_CHANCE = rootNode.getNode("beetroot", "chance").getInt();
            BEETROOT_DROP_CHANCE_OF = rootNode.getNode("beetroot", "chance_of").getInt();

            HARVEST_LIST_ENABLED = rootNode.getNode("list", "enabled").getBoolean(false);
            HARVEST_TOOLS.clear();
            if (HARVEST_LIST_ENABLED) {
                HARVEST_LIST_IS_WHITELIST = rootNode.getNode("list", "is_whitelist").getBoolean(true);
                try {
                    HARVEST_TOOLS = rootNode.getNode("list", "tools").getList(TypeTokens.STRING_TOKEN);
                } catch (ObjectMappingException e) {
                    plugin.getLogger().error("An error occurred while reading the list of tools for FastHarvest");
                    e.printStackTrace();
                }
            }
        }
    }

    private static void loadBlocksDrops(BoxOUtils plugin) throws IOException {
        ConfigurationNode rootNode = loadFileSafely(plugin, "blocksdrops.conf", "BlocksDrops");

        Config.CUSTOM_BLOCKS_DROPS_ENABLED = rootNode.getNode("enabled").getBoolean(false);
        Config.CUSTOM_BLOCKS_DROPS.clear();
        if (Config.CUSTOM_BLOCKS_DROPS_ENABLED) {
            if (!blockDropsListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new CustomBlockDropsListener());
                blockDropsListenersRegistered = true;
            }

            for (Map.Entry<Object, ? extends ConfigurationNode> child : rootNode.getNode("blocks").getChildrenMap().entrySet()) {
                List<ItemLoot> itemLoots = new ArrayList<>();
                readDrops(plugin, child, itemLoots);
                ConfigurationNode node = child.getValue();
                CustomLoot.Reuse reuse = null;
                if (!node.getNode("reuse").isVirtual()) {
                    reuse = readReuse(plugin, node.getNode("reuse"));
                }

                ConfigurationNode requirementsNode = node.getNode("requirements");
                List<CustomLootRequirement<?>> requirements = !requirementsNode.isVirtual() ? readRequirements(plugin, requirementsNode) : Collections.emptyList();

                Config.CUSTOM_BLOCKS_DROPS.put((String) child.getKey(),
                        new CustomLoot(itemLoots, node.getNode("experience").getInt(), node.getNode("overwrite").getBoolean(false),
                                node.getNode("exp-overwrite").getBoolean(false), requirements, readMoneyLoot(plugin, node.getNode("money")), reuse));
            }
        }
    }

    private static void loadMobsDrops(BoxOUtils plugin) throws IOException {
        ConfigurationNode rootNode = loadFileSafely(plugin, "mobsdrops.conf", "MobsDrops");

        Config.CUSTOM_MOBS_DROPS_ENABLED = rootNode.getNode("enabled").getBoolean(false);
        Config.CUSTOM_MOBS_DROPS.clear();
        if (Config.CUSTOM_MOBS_DROPS_ENABLED) {
            if (!mobDropsListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new CustomMobDropsListener());
                mobDropsListenersRegistered = true;
            }

            for (Map.Entry<Object, ? extends ConfigurationNode> child : rootNode.getNode("mobs").getChildrenMap().entrySet()) {
                List<ItemLoot> itemLoots = new ArrayList<>();
                readDrops(plugin, child, itemLoots);
                ConfigurationNode node = child.getValue();
                CustomLoot.Reuse reuse = null;
                if (!node.getNode("reuse").isVirtual()) {
                    reuse = readReuse(plugin, node.getNode("reuse"));
                }

                ConfigurationNode requirementsNode = node.getNode("requirements");
                List<CustomLootRequirement<?>> requirements = !requirementsNode.isVirtual() ? readRequirements(plugin, requirementsNode) : Collections.emptyList();

                Config.CUSTOM_MOBS_DROPS.put((String) child.getKey(), new CustomLoot(itemLoots, node.getNode("experience").getInt(),
                        node.getNode("overwrite").getBoolean(false), node.getNode("exp-overwrite").getBoolean(false), requirements, readMoneyLoot(plugin, node.getNode("money")), reuse));
            }
        }
    }

    private static void loadBlockSpawners(BoxOUtils plugin) throws IOException {
        ConfigurationNode rootNode = loadFileSafely(plugin, "blockspawners.conf", "BlockSpawners");

        Config.BLOCK_SPAWNERS_ENABLED = rootNode.getNode("enabled").getBoolean(false);
        Config.BLOCK_SPAWNERS_DROPS.clear();
        if (Config.BLOCK_SPAWNERS_ENABLED) {
            if (!blockSpawnersListenersRegistered) {
                Sponge.getEventManager().registerListeners(plugin, new BlockSpawnersListener());
                blockSpawnersListenersRegistered = true;
            }

            for (Map.Entry<Object, ? extends ConfigurationNode> child : rootNode.getNode("blocks").getChildrenMap().entrySet()) {
                ConfigurationNode node = child.getValue();
                List<SpawnedMob> spawnedMobs = new ArrayList<>();
                node.getNode("spawns").getChildrenList().forEach(spawn -> {
                    String mobType = spawn.getNode("type").getString();
                    if (mobType == null) {
                        plugin.getLogger().error("A BlockSpawner spawn for '{}' has no 'type'", child.getKey());
                        return;
                    }

                    IIntQuantity quantity = null;
                    ConfigurationNode quantityNode = spawn.getNode("quantity");
                    if (!quantityNode.isVirtual())
                        quantity = readQuantity(plugin, quantityNode, BlockSpawnerFixedQuantityErrorReporter.INSTANCE, BlockSpawnerBoundedQuantityErrorReporter.INSTANCE);

                    if (quantity instanceof BoundedIntQuantity) {
                        BoundedIntQuantity boundedQuantity = (BoundedIntQuantity) quantity;
                        if (boundedQuantity.getFrom() < 0) {
                            plugin.getLogger().error("The quantity lower bound ({}) of BlockSpawner '{}' for mob '{}' is negative. This spawn will not be loaded.",
                                    boundedQuantity.getFrom(), mobType, child.getKey());
                            return;
                        }

                        if (boundedQuantity.getTo() < boundedQuantity.getFrom()) {
                            plugin.getLogger().error("The quantity upper bound ({}) of BlockSpawner '{}' for mob '{}' is less than its lower bound ({}). This spawn will not be loaded.",
                                    boundedQuantity.getTo(), mobType, child.getKey(), boundedQuantity.getFrom());
                            return;
                        }
                    }

                    double chance = 0;
                    ConfigurationNode chanceNode = spawn.getNode("chance");
                    if (!chanceNode.isVirtual()) {
                        chance = chanceNode.getDouble(Double.NaN);
                        if (Double.isNaN(chance)) {
                            plugin.getLogger().error("Chance of BlockSpawner mob '{}' for block '{}' is not a valid number ('{}'). This spawn will not be loaded.",
                                    mobType, child.getKey(), chanceNode.getValue());
                            return;
                        }
                    }

                    spawnedMobs.add(new SpawnedMob(mobType, chance, quantity));
                });
                Config.BLOCK_SPAWNERS_DROPS.put((String) child.getKey(), spawnedMobs);
            }
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

        return HoconConfigurationLoader.builder().setPath(destFile).build().load();
    }

    private static List<CustomLootRequirement<?>> readRequirements(BoxOUtils plugin, ConfigurationNode requirementsNode) {
        List<CustomLootRequirement<?>> requirements = new ArrayList<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> requirement : requirementsNode.getChildrenMap().entrySet()) {
            String key = requirement.getKey().toString();
            ConfigurationNode value = requirement.getValue();

            CustomLootRequirementProvider requirementProvider = IntegrationsManager.INSTANCE.getRequirementProvider(key);
            if (requirementProvider != null) {
                try {
                    requirements.add(requirementProvider.provide(value));
                } catch (RequirementConfigurationException e) {
                    plugin.getLogger().error("Unable to read requirement", e);
                }
            }
        }

        return requirements;
    }

    private static CustomLoot.Reuse readReuse(BoxOUtils plugin, ConfigurationNode reuseNode) {
        float multiplier = reuseNode.getNode("multiplier").getFloat(1);
        Map<String, LootReuse> reuses = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : reuseNode.getNode("items").getChildrenMap().entrySet()) {
            String itemId = SpongeUtils.addMcNamespaceIfNeeded(entry.getKey().toString());
            LootReuse reuse = readLootReuse(plugin, entry.getValue());
            if (reuse != null) {
                reuses.put(itemId, reuse);
            }
        }

        ConfigurationNode requirementsNode = reuseNode.getNode("requirements");
        List<CustomLootRequirement<?>> requirements = !requirementsNode.isVirtual() ? readRequirements(plugin, requirementsNode) : Collections.emptyList();
        return new CustomLoot.Reuse(multiplier, reuses, requirements);
    }

    @Nullable
    private static LootReuse readLootReuse(BoxOUtils plugin, ConfigurationNode itemNode) {
        ConfigurationNode multiplierNode = itemNode.getNode("multiplier");
        if (!multiplierNode.isVirtual()) {
            return new MultiplyLootReuse(multiplierNode.getFloat());
        }

        ConfigurationNode quantityNode = itemNode.getNode("quantity");
        if (!quantityNode.isVirtual()) {
            IIntQuantity quantity = readQuantity(plugin, quantityNode, null, null);
            if (quantity == null)
                return null;

            return new SimpleLootReuse(quantity);
        }

        return null;
    }

    private static void readDrops(BoxOUtils plugin, Map.Entry<Object, ? extends ConfigurationNode> child, List<ItemLoot> itemLoots) {
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
                plugin.getLogger().error("The CustomDrop for '" + child.getKey() + "' does not have a 'type'. It will not be loaded.");
                return;
            }

            IIntQuantity quantity = null;
            ConfigurationNode quantityNode = customLoot.getNode("quantity");
            if (!quantityNode.isVirtual())
                quantity = readQuantity(plugin, quantityNode, CustomDropFixedQuantityErrorReporter.INSTANCE, CustomDropBoundedQuantityErrorReporter.INSTANCE);

            if (quantity instanceof BoundedIntQuantity) {
                BoundedIntQuantity boundedQuantity = (BoundedIntQuantity) quantity;
                if (boundedQuantity.getFrom() < 0) {
                    plugin.getLogger().error("The quantity lower bound ({}) of CustomDrop '{}' for '{}' is negative. This drop will not be loaded.",
                            boundedQuantity.getFrom(), itemId, child.getKey());
                    return;
                }

                if (boundedQuantity.getTo() < boundedQuantity.getFrom()) {
                    plugin.getLogger().error("The quantity upper bound ({}) of CustomDrop '{}' for '{}' is less than its lower bound ({}). This drop will not be loaded.",
                            boundedQuantity.getTo(), itemId, child.getKey(), boundedQuantity.getFrom());
                    return;
                }
            }

            double chance = 0;
            ConfigurationNode chanceNode = customLoot.getNode("chance");
            if (!chanceNode.isVirtual()) {
                chance = chanceNode.getDouble(Double.NaN);
                if (Double.isNaN(chance)) {
                    plugin.getLogger().error("Chance of CustomDrop '{}' of block '{}' is not a valid number ('{}'). This spawn will not be loaded.",
                            itemId, child.getKey(), chanceNode.getValue());
                    return;
                }
            }

            itemLoots.add(new ItemLoot(itemId, providerId, customLoot.getNode("displayname").getString(), chance, quantity));
        });
    }

    @Nullable
    private static MoneyLoot readMoneyLoot(BoxOUtils plugin, ConfigurationNode moneyNode) {
        if (moneyNode.isVirtual())
            return null;

        ConfigurationNode amountNode = moneyNode.getNode("amount");
        if (amountNode.isVirtual()) {
            plugin.getLogger().error("No money amount set for '{}'. No money will be given for this CustomDrop.", moneyNode.getPath()[1]);
            return null;
        }

        IIntQuantity quantity = readQuantity(plugin, amountNode, MoneyFixedQuantityErrorReporter.INSTANCE, MoneyBoundedQuantityErrorReporter.INSTANCE);
        if (quantity == null)
            return null;

        if (quantity instanceof BoundedIntQuantity) {
            BoundedIntQuantity boundedQuantity = (BoundedIntQuantity) quantity;
            if (boundedQuantity.getFrom() < 0) {
                plugin.getLogger().error("The money amount lower bound ({}) for '{}' is negative. This drop will not be loaded.",
                        boundedQuantity.getFrom(), moneyNode.getPath()[1]);
                return null;
            }

            if (boundedQuantity.getTo() < boundedQuantity.getFrom()) {
                plugin.getLogger().error("The quantity upper bound ({}) for '{}' is less than its lower bound ({}). This drop will not be loaded.",
                        boundedQuantity.getTo(), moneyNode.getPath()[1], boundedQuantity.getFrom());
                return null;
            }
        }

        double chance = 0;
        ConfigurationNode chanceNode = moneyNode.getNode("chance");
        if (!chanceNode.isVirtual()) {
            chance = chanceNode.getDouble(Double.NaN);
            if (Double.isNaN(chance)) {
                plugin.getLogger().error("The money reward chance of CustomDrop block '{}' is not a valid number ('{}'). This spawn will not be loaded.",
                        moneyNode.getPath()[1], chanceNode.getValue());
                return null;
            }
        }

        return new MoneyLoot(quantity, moneyNode.getNode("currency").getString(), chance, moneyNode.getNode("message").getString());
    }

    @Nullable
    private static IIntQuantity readQuantity(BoxOUtils plugin, ConfigurationNode amountNode,
            @Nullable ErrorReporter fixedQuantityErrorReporter, @Nullable ErrorReporter boundedQuantityErrorReporter) {
        int amount = amountNode.getInt();
        // 0 means the value cannot be read as int, so we assume it is a bounded quantity
        if (amount == 0) {
            String bounds = amountNode.getString();
            if (bounds == null) {
                if (boundedQuantityErrorReporter != null)
                    boundedQuantityErrorReporter.report(plugin, amountNode);

                return null;
            }

            try {
                return BoundedIntQuantity.parse(bounds);
            } catch (NumberFormatException e) {
                if (boundedQuantityErrorReporter != null)
                    boundedQuantityErrorReporter.report(plugin, amountNode);

                return null;
            }
        } else {
            if (amount < 0) {
                if (fixedQuantityErrorReporter != null)
                    fixedQuantityErrorReporter.report(plugin, amountNode);

                return null;
            }

            return new FixedIntQuantity(amount);
        }
    }

    private interface ErrorReporter {

        void report(BoxOUtils plugin, ConfigurationNode node);
    }

    private static class CustomDropFixedQuantityErrorReporter implements ErrorReporter {

        public static final ErrorReporter INSTANCE = new CustomDropFixedQuantityErrorReporter();

        @Override
        public void report(BoxOUtils plugin, ConfigurationNode configurationNode) {
            plugin.getLogger().error("Invalid CustomDrop fixed quantity for '{}'. This drop will not be loaded.", configurationNode.getPath()[1]);
        }
    }

    private static class CustomDropBoundedQuantityErrorReporter implements ErrorReporter {

        public static final ErrorReporter INSTANCE = new CustomDropBoundedQuantityErrorReporter();

        @Override
        public void report(BoxOUtils plugin, ConfigurationNode configurationNode) {
            plugin.getLogger().error("Invalid CustomDrop bounded quantity for '{}'. This drop will not be loaded.", configurationNode.getPath()[1]);
        }
    }

    private static class MoneyFixedQuantityErrorReporter implements ErrorReporter {

        public static final ErrorReporter INSTANCE = new MoneyFixedQuantityErrorReporter();

        @Override
        public void report(BoxOUtils plugin, ConfigurationNode configurationNode) {
            plugin.getLogger().error("Invalid money amount for '{}'. No money will be given for this CustomDrop.", configurationNode.getPath()[1]);
        }
    }

    private static class MoneyBoundedQuantityErrorReporter implements ErrorReporter {

        public static final ErrorReporter INSTANCE = new MoneyBoundedQuantityErrorReporter();

        @Override
        public void report(BoxOUtils plugin, ConfigurationNode configurationNode) {
            plugin.getLogger().error("Invalid bounded money amount for '{}'. No money will be given for this CustomDrop.", configurationNode.getPath()[1]);
        }
    }

    private static class BlockSpawnerFixedQuantityErrorReporter implements ErrorReporter {

        public static final ErrorReporter INSTANCE = new BlockSpawnerFixedQuantityErrorReporter();

        @Override
        public void report(BoxOUtils plugin, ConfigurationNode configurationNode) {
            ConfigurationNode parent = configurationNode.getParent();
            String mobType = parent != null ? parent.getNode("type").getString("unknown") : "unknown";
            plugin.getLogger().error("Invalid BlockSpawner fixed quantity of mob '{}' for '{}'. This spawner will not be loaded.", mobType, configurationNode.getPath()[1]);
        }
    }

    private static class BlockSpawnerBoundedQuantityErrorReporter implements ErrorReporter {

        public static final ErrorReporter INSTANCE = new BlockSpawnerBoundedQuantityErrorReporter();

        @Override
        public void report(BoxOUtils plugin, ConfigurationNode configurationNode) {
            ConfigurationNode parent = configurationNode.getParent();
            String mobType = parent != null ? parent.getNode("type").getString("unknown") : "unknown";
            plugin.getLogger().error("Invalid BlockSpawner bounded quantity of mob '{}' for '{}'. This spawner will not be loaded.", mobType, configurationNode.getPath()[1]);
        }
    }
}
