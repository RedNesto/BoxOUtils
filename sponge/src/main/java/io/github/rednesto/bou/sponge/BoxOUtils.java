/*
 * MIT License
 *
 * Copyright (c) [year] [fullname]
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

import com.google.inject.Inject;
import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.CustomLoot;
import io.github.rednesto.bou.common.ItemLoot;
import io.github.rednesto.bou.sponge.listeners.CustomBlockDropsListener;
import io.github.rednesto.bou.sponge.listeners.CustomMobDropsListener;
import io.github.rednesto.bou.sponge.listeners.FastHarvestListener;
import io.github.rednesto.fileinventories.api.FileInventoriesService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.util.TypeTokens;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.rednesto.bou.common.Config.*;

@Plugin(
        id = "box-o-utils",
        name = "Box O' Utils",
        url = "https://rednesto.github.io/box-o-utils",
        description = "Control what each blocks/mobs loots, right-click to harvest, etc...",
        authors = {
                "RedNesto"
        }
)
public class BoxOUtils {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private static BoxOUtils instance;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;
        try {
            loadConf();
        } catch(IOException e) {
            this.logger.error("Cannot load configuration");
            e.printStackTrace();
        }
    }

    private void loadConf() throws IOException {
        Files.createDirectories(configDir);

        File fastHarvestConfFile = new File(this.configDir.toFile(), "fastharvest.conf");

        if(!fastHarvestConfFile.exists())
            Files.copy(getClass().getResourceAsStream("/fastharvest.conf"), fastHarvestConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ConfigurationNode fastHarvestConf = HoconConfigurationLoader.builder().setFile(fastHarvestConfFile).build().load();

        if(fastHarvestConf.getNode("enabled").getBoolean(false)) {
            Sponge.getEventManager().registerListeners(this, new FastHarvestListener());

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

            if(fastHarvestConf.getNode("list", "enabled").getBoolean(false)) {
                HARVEST_LIST_ENABLED = true;

                HARVEST_LIST_IS_WHITELIST = fastHarvestConf.getNode("list", "is_whitelist").getBoolean(true);
                try {
                    HARVEST_TOOLS = fastHarvestConf.getNode("list", "tools").getList(TypeTokens.STRING_TOKEN);
                } catch (ObjectMappingException e) {
                    this.logger.error("An error occurred while reading the list of tools for FastHarvest");
                    e.printStackTrace();
                }
            }
        }

        File blocksDropsConfFile = new File(this.configDir.toFile(), "blocksdrops.conf");

        if(!blocksDropsConfFile.exists())
            Files.copy(getClass().getResourceAsStream("/blocksdrops.conf"), blocksDropsConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ConfigurationNode blocksDropsConf = HoconConfigurationLoader.builder().setFile(blocksDropsConfFile).build().load();

        if(blocksDropsConf.getNode("enabled").getBoolean(false)) {
            Config.CUSTOM_BLOCKS_DROPS_ENABLED = true;

            Sponge.getEventManager().registerListeners(this, new CustomBlockDropsListener());

            for (Map.Entry<Object, ? extends ConfigurationNode> child : blocksDropsConf.getNode("blocks").getChildrenMap().entrySet()) {
                List<ItemLoot> itemLoots = new ArrayList<>();
                readDrops(child, itemLoots);
                ConfigurationNode node = child.getValue();
                Config.CUSTOM_BLOCKS_DROPS.put((String) child.getKey(), new CustomLoot(itemLoots, node.getNode("experience").getInt(), node.getNode("overwrite").getBoolean(false), node.getNode("exp-overwrite").getBoolean(false)));
            }
        }

        File mobsDropsConfFile = new File(this.configDir.toFile(), "mobsdrops.conf");

        if(!mobsDropsConfFile.exists())
            Files.copy(getClass().getResourceAsStream("/mobsdrops.conf"), mobsDropsConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ConfigurationNode mobsDropsConf = HoconConfigurationLoader.builder().setFile(mobsDropsConfFile).build().load();

        if(mobsDropsConf.getNode("enabled").getBoolean(false)) {
            Config.CUSTOM_MOBS_DROPS_ENABLED = true;

            Sponge.getEventManager().registerListeners(this, new CustomMobDropsListener());

            for (Map.Entry<Object, ? extends ConfigurationNode> child : mobsDropsConf.getNode("mobs").getChildrenMap().entrySet()) {
                List<ItemLoot> itemLoots = new ArrayList<>();
                readDrops(child, itemLoots);
                ConfigurationNode node = child.getValue();
                Config.CUSTOM_MOBS_DROPS.put((String) child.getKey(), new CustomLoot(itemLoots, node.getNode("experience").getInt(), node.getNode("overwrite").getBoolean(false), node.getNode("exp-overwrite").getBoolean(false)));
            }
        }

        if(Config.CUSTOM_BLOCKS_DROPS_ENABLED || Config.CUSTOM_MOBS_DROPS_ENABLED) {
            Sponge.getServiceManager().provide(FileInventoriesService.class).ifPresent(service -> {
                File fileitems = new File(this.configDir.toFile(), "fileitems");
                if(fileitems.exists() && fileitems.isDirectory()) {
                    for(File file : fileitems.listFiles(file -> file.isFile() && file.getName().endsWith(".json"))) {
                        try {
                            service.load(FileInventoriesService.LoadTarget.LOAD_ITEMS, file.toPath());
                        } catch (IOException e) {
                            this.logger.error("Cannot load configuration");
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void readDrops(Map.Entry<Object, ? extends ConfigurationNode> child, List<ItemLoot> itemLoots) {
        child.getValue().getNode("drops").getChildrenList().forEach(customLoot -> {
            ItemLoot.Type type;
            if(customLoot.getNode("file_inv_id").isVirtual()) {
                type = ItemLoot.Type.CLASSIC;
            } else {
                type = ItemLoot.Type.FILE_INVENTORIES;
            }
            String[] quantityBounds = customLoot.getNode("quantity").getString("1-1").split("-");
            switch(type) {
                case CLASSIC:
                    itemLoots.add(new ItemLoot(customLoot.getNode("type").getString(), type, customLoot.getNode("chance").getInt(), Integer.parseInt(quantityBounds[0]), Integer.parseInt(quantityBounds[1])));
                    break;
                case FILE_INVENTORIES:
                    itemLoots.add(new ItemLoot(customLoot.getNode("file_inv_id").getString(), type, customLoot.getNode("chance").getInt(), Integer.parseInt(quantityBounds[0]), Integer.parseInt(quantityBounds[1])));
                    break;
            }
        });
    }

    public static BoxOUtils getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }
}
