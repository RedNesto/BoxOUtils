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
package io.github.rednesto.bou.spigot;

import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.CustomLoot;
import io.github.rednesto.bou.common.ItemLoot;
import io.github.rednesto.bou.spigot.events.CustomBlockDropsListener;
import io.github.rednesto.bou.spigot.events.CustomMobDropsListener;
import io.github.rednesto.bou.spigot.events.FastHarvestListener;
import io.github.rednesto.fileinventories.api.FileInventories;
import io.github.rednesto.fileinventories.api.FileInventoriesService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.rednesto.bou.common.Config.*;

public final class BoxOUtils extends JavaPlugin {

    private static BoxOUtils instance;

    @Override
    public void onEnable() {
        instance = this;
        try {
            loadConf();
        } catch(IOException e) {
            this.getLogger().severe("Cannot load configuration");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
    }

    private void loadConf() throws IOException {
        this.getDataFolder().mkdir();

        File fastHarvestConfFile = new File(this.getDataFolder(), "fastharvest.yml");

        if(!fastHarvestConfFile.exists())
            Files.copy(getClass().getResourceAsStream("/fastharvest.yml"), fastHarvestConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        FileConfiguration fastHarvestConf = YamlConfiguration.loadConfiguration(fastHarvestConfFile);

        if(fastHarvestConf.getBoolean("enabled")) {
            Bukkit.getPluginManager().registerEvents(new FastHarvestListener(), this);

            SEED_DROP_MINIMUM = fastHarvestConf.getInt("seed.minimum");
            SEED_DROP_COUNT = fastHarvestConf.getInt("seed.count");
            SEED_DROP_FORTUNE_FACTOR = fastHarvestConf.getInt("seed.fortune_factor");
            SEED_DROP_CHANCE = fastHarvestConf.getInt("seed.chance");
            SEED_DROP_CHANCE_OF = fastHarvestConf.getInt("seed.chance_of");

            WHEAT_DROP_MINIMUM = fastHarvestConf.getInt("wheat.minimum");
            WHEAT_DROP_COUNT = fastHarvestConf.getInt("wheat.count");
            WHEAT_DROP_FORTUNE_FACTOR = fastHarvestConf.getInt("wheat.fortune_factor");
            WHEAT_DROP_CHANCE = fastHarvestConf.getInt("wheat.chance");
            WHEAT_DROP_CHANCE_OF = fastHarvestConf.getInt("wheat.chance_of");

            CARROT_DROP_MINIMUM = fastHarvestConf.getInt("carrot.minimum");
            CARROT_DROP_COUNT = fastHarvestConf.getInt("carrot.count");
            CARROT_DROP_FORTUNE_FACTOR = fastHarvestConf.getInt("carrot.fortune_factor");
            CARROT_DROP_CHANCE = fastHarvestConf.getInt("carrot.chance");
            CARROT_DROP_CHANCE_OF = fastHarvestConf.getInt("carrot.chance_of");

            POTATO_DROP_MINIMUM = fastHarvestConf.getInt("potato.minimum");
            POTATO_DROP_COUNT = fastHarvestConf.getInt("potato.count");
            POTATO_DROP_FORTUNE_FACTOR = fastHarvestConf.getInt("potato.fortune_factor");
            POTATO_DROP_CHANCE = fastHarvestConf.getInt("potato.chance");
            POTATO_DROP_CHANCE_OF = fastHarvestConf.getInt("potato.chance_of");

            BEETROOT_SEED_DROP_MINIMUM = fastHarvestConf.getInt("beetroot_seed.minimum");
            BEETROOT_SEED_DROP_COUNT = fastHarvestConf.getInt("beetroot_seed.count");
            BEETROOT_SEED_DROP_FORTUNE_FACTOR = fastHarvestConf.getInt("beetroot_seed.fortune_factor");
            BEETROOT_SEED_DROP_CHANCE = fastHarvestConf.getInt("beetroot_seed.chance");
            BEETROOT_SEED_DROP_CHANCE_OF = fastHarvestConf.getInt("beetroot_seed.chance_of");

            BEETROOT_DROP_MINIMUM = fastHarvestConf.getInt("beetroot.minimum");
            BEETROOT_DROP_COUNT = fastHarvestConf.getInt("beetroot.count");
            BEETROOT_DROP_FORTUNE_FACTOR = fastHarvestConf.getInt("beetroot.fortune_factor");
            BEETROOT_DROP_CHANCE = fastHarvestConf.getInt("beetroot.chance");
            BEETROOT_DROP_CHANCE_OF = fastHarvestConf.getInt("beetroot.chance_of");

            HARVEST_LIST_ENABLED = fastHarvestConf.getBoolean("list.enabled");
            HARVEST_LIST_IS_WHITELIST = fastHarvestConf.getBoolean("list.is_whitelist");
            HARVEST_TOOLS = fastHarvestConf.getStringList("list.tools");
        }

        File blocksDropsConfFile = new File(this.getDataFolder(), "blocksdrops.yml");

        if(!blocksDropsConfFile.exists())
            Files.copy(getClass().getResourceAsStream("/blocksdrops.yml"), blocksDropsConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        FileConfiguration blocksDropsConf = YamlConfiguration.loadConfiguration(blocksDropsConfFile);

        if(blocksDropsConf.getBoolean("enabled")) {
            Config.CUSTOM_BLOCKS_DROPS_ENABLED = true;

            Bukkit.getPluginManager().registerEvents(new CustomBlockDropsListener(), this);

            for (String key : ((MemorySection) blocksDropsConf.get("blocks")).getKeys(false)) {
                //noinspection unchecked
                Config.CUSTOM_BLOCKS_DROPS.put(key, new CustomLoot(((List<Map<String, Object>>) blocksDropsConf.getList("blocks." + key + ".drops", new ArrayList<>())).stream().map(drop -> {
                    String[] quantitySplit = ((String) drop.getOrDefault("quantity", "1-1")).split("-");
                    if (drop.containsKey("file_inv_id")) {
                        return new ItemLoot((String) drop.get("file_inv_id"), ItemLoot.Type.FILE_INVENTORIES, null, (int) drop.getOrDefault("chance", -1), Integer.parseInt(quantitySplit[0]), Integer.parseInt(quantitySplit[1]));
                    } else {
                        return new ItemLoot((String) drop.get("type"), ItemLoot.Type.CLASSIC, String.valueOf(drop.get("displayname")), (int) drop.getOrDefault("chance", -1), Integer.parseInt(quantitySplit[0]), Integer.parseInt(quantitySplit[1]));
                    }
                }).collect(Collectors.toList()), blocksDropsConf.getInt("blocks." + key + ".experience", 0), blocksDropsConf.getBoolean("blocks." + key + ".overwrite", false), blocksDropsConf.getBoolean("blocks." + key + ".exp-overwrite", false)));
            }
        }

        File mobsDropsConfFile = new File(this.getDataFolder(), "mobsdrops.yml");

        if(!mobsDropsConfFile.exists())
            Files.copy(getClass().getResourceAsStream("/mobsdrops.yml"), mobsDropsConfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        FileConfiguration mobsDropsConf = YamlConfiguration.loadConfiguration(mobsDropsConfFile);

        if(mobsDropsConf.getBoolean("enabled")) {
            Config.CUSTOM_MOBS_DROPS_ENABLED = true;

            Bukkit.getPluginManager().registerEvents(new CustomMobDropsListener(), this);

            for (String key : ((MemorySection) mobsDropsConf.get("mobs")).getKeys(false)) {
                //noinspection unchecked
                Config.CUSTOM_MOBS_DROPS.put(key, new CustomLoot(((List<Map<String, Object>>) mobsDropsConf.getList("mobs." + key + ".drops", new ArrayList<>())).stream().map(drop -> {
                    String[] quantitySplit = ((String) drop.getOrDefault("quantity", "1-1")).split("-");
                    if (drop.containsKey("file_inv_id")) {
                        return new ItemLoot((String) drop.get("file_inv_id"), ItemLoot.Type.FILE_INVENTORIES, String.valueOf(drop.get("displayname")), (int) drop.getOrDefault("chance", -1), Integer.parseInt(quantitySplit[0]), Integer.parseInt(quantitySplit[1]));
                    } else {
                        return new ItemLoot((String) drop.get("type"), ItemLoot.Type.CLASSIC, String.valueOf(drop.get("displayname")), (int) drop.getOrDefault("chance", -1), Integer.parseInt(quantitySplit[0]), Integer.parseInt(quantitySplit[1]));
                    }
                }).collect(Collectors.toList()), mobsDropsConf.getInt("mobs." + key + ".experience", 0), mobsDropsConf.getBoolean("mobs." + key + ".overwrite", false), mobsDropsConf.getBoolean("mobs." + key + ".exp-overwrite", false)));
            }
        }

        if(Config.CUSTOM_BLOCKS_DROPS_ENABLED || Config.CUSTOM_MOBS_DROPS_ENABLED) {
            FileInventoriesService service = FileInventories.getService();
            File fileitems = new File(getDataFolder(), "fileitems");
            if(service != null && fileitems.exists()) {
                for(File file : fileitems.listFiles(file -> file.isFile() && file.getName().endsWith(".json"))) {
                    service.load(FileInventoriesService.LoadTarget.LOAD_ITEMS, file);
                }
            }
        }
    }

    public static BoxOUtils getInstance() {
        return instance;
    }
}
