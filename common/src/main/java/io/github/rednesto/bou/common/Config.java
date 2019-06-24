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
package io.github.rednesto.bou.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    // TODO add a way to enable/disable the removal of a seed due to seeding

    /*
     * FAST HARVEST
     */

    /**
     * True if the Fast Harvest is enabled
     */
    public static boolean FAST_HARVEST_ENABLED = false;

    /**
     * Minimum amount of seeds which will be dropped
     */
    public static int SEED_DROP_MINIMUM = 0;
    /**
     * Count of seeds which will pass through a random calculation to have a chance to be dropped
     */
    public static int SEED_DROP_COUNT = 3;
    /**
     * The factor used to multiply the quantity of every seed added via the fortune enchantment
     */
    public static int SEED_DROP_FORTUNE_FACTOR = 1;
    /**
     * Chances the seed processed will be dropped
     */
    public static int SEED_DROP_CHANCE = -1;
    /**
     * Value used by the random calculation to know if a seed will be droppped
     */
    public static int SEED_DROP_CHANCE_OF = -1;

    /**
     * Minimum amount of wheat which will be dropped
     */
    public static int WHEAT_DROP_MINIMUM = 1;
    /**
     * Count of wheat which will pass through a random calculation to have a chance to be dropped
     */
    public static int WHEAT_DROP_COUNT = 0;
    /**
     * The factor used to multiply the quantity of every wheat added via the fortune enchantment
     */
    public static int WHEAT_DROP_FORTUNE_FACTOR = 1;
    /**
     * Chances the wheat processed will be dropped
     */
    public static int WHEAT_DROP_CHANCE = -1;
    /**
     * Value used by the random calculation to know if a wheat will be droppped
     */
    public static int WHEAT_DROP_CHANCE_OF = -1;

    /**
     * Minimum amount of carrots which will be dropped
     */
    public static int CARROT_DROP_MINIMUM = 1;
    /**
     * Count of carrots which will pass through a random calculation to have a chance to be dropped
     */
    public static int CARROT_DROP_COUNT = 3;
    /**
     * The factor used to multiply the quantity of every carrot added via the fortune enchantment
     */
    public static int CARROT_DROP_FORTUNE_FACTOR = 1;
    /**
     * Chances the carrot processed will be dropped
     */
    public static int CARROT_DROP_CHANCE = -1;
    /**
     * Value used by the random calculation to know if a carrot will be droppped
     */
    public static int CARROT_DROP_CHANCE_OF = -1;

    /**
     * Minimum amount of potatoes which will be dropped
     */
    public static int POTATO_DROP_MINIMUM = 1;
    /**
     * Count of potatoes which will pass through a random calculation to have a chance to be dropped
     */
    public static int POTATO_DROP_COUNT = 3;
    /**
     * The factor used to multiply the quantity of every potato added via the fortune enchantment
     */
    public static int POTATO_DROP_FORTUNE_FACTOR = 1;
    /**
     * Chances the potato processed will be dropped
     */
    public static int POTATO_DROP_CHANCE = -1;
    /**
     * Value used by the random calculation to know if a potato will be droppped
     */
    public static int POTATO_DROP_CHANCE_OF = -1;

    /**
     * Minimum amount of beetroot seeds which will be dropped
     */
    public static int BEETROOT_SEED_DROP_MINIMUM = 1;
    /**
     * Count of beetroot seeds which will pass through a random calculation to have a chance to be dropped
     */
    public static int BEETROOT_SEED_DROP_COUNT = 3;
    /**
     * The factor used to multiply the quantity of every beetroot seed added via the fortune enchantment
     */
    public static int BEETROOT_SEED_DROP_FORTUNE_FACTOR = 1;
    /**
     * Chances the beetroot seed processed will be dropped
     */
    public static int BEETROOT_SEED_DROP_CHANCE = -1;
    /**
     * Value used by the random calculation to know if a beetroot seed will be droppped
     */
    public static int BEETROOT_SEED_DROP_CHANCE_OF = -1;

    /**
     * Minimum amount of beetroots which will be dropped
     */
    public static int BEETROOT_DROP_MINIMUM = 1;
    /**
     * Count of beetroots which will pass through a random calculation to have a chance to be dropped
     */
    public static int BEETROOT_DROP_COUNT = 0;
    /**
     * The factor used to multiply the quantity of every beetroot added via the fortune enchantment
     */
    public static int BEETROOT_DROP_FORTUNE_FACTOR = 1;
    /**
     * Chances the beetroot processed will be dropped
     */
    public static int BEETROOT_DROP_CHANCE = -1;
    /**
     * Value used by the random calculation to know if a beetroot will be droppped
     */
    public static int BEETROOT_DROP_CHANCE_OF = -1;

    /**
     * If the plugin use the given list of tools to restrict fast harvest
     */
    public static boolean HARVEST_LIST_ENABLED = false;

    /**
     * If the harvest list is a whitelist or a blacklist
     */
    public static boolean HARVEST_LIST_IS_WHITELIST = true;

    /**
     * A list of all the whitelisted/blacklisted items used to harvest crops
     */
    public static List<String> HARVEST_TOOLS = new ArrayList<>();

    /*
     * CUSTOM MOBS DROPS
     */

    /**
     * True if the custom blocks drops are enabled
     */
    public static boolean CUSTOM_BLOCKS_DROPS_ENABLED = false;

    /**
     * A Map which have for key the ID and for value the list of items to loot
     */
    public static Map<String, CustomLoot> CUSTOM_BLOCKS_DROPS = new HashMap<>();

    /*
     * CUSTOM BLOCKS DROPS
     */

    /**
     * True if the custom mobs drops are enabled
     */
    public static boolean CUSTOM_MOBS_DROPS_ENABLED = false;

    /**
     * A Map which have for key the ID and for value the list of items to loot
     */
    public static Map<String, CustomLoot> CUSTOM_MOBS_DROPS = new HashMap<>();

    /*
     * BLOCK SPAWNERS
     */

    /**
     * True if the block spawners are enabled
     */
    public static boolean BLOCK_SPAWNERS_ENABLED = false;

    /**
     * A Map which have for key the ID and for value the list of items to loot
     */
    public static Map<String, List<SpawnedMob>> BLOCK_SPAWNERS_DROPS = new HashMap<>();

    private Config() {}

    public static boolean canHarvest(String item) {
        return !HARVEST_LIST_ENABLED
                || HARVEST_LIST_IS_WHITELIST && HARVEST_TOOLS.contains(item)
                || !HARVEST_LIST_IS_WHITELIST && !HARVEST_TOOLS.contains(item);
    }
}
