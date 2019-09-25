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

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.Config;
import io.github.rednesto.bou.api.blockspawners.SpawnedMob;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.customdrops.CustomLootCommand;
import io.github.rednesto.bou.api.customdrops.ItemLoot;
import io.github.rednesto.bou.api.customdrops.MoneyLoot;
import io.github.rednesto.bou.api.fastharvest.FastHarvestCrop;
import io.github.rednesto.bou.api.fastharvest.FastHarvestTools;
import io.github.rednesto.bou.api.lootReuse.LootReuse;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import io.github.rednesto.bou.api.range.IntRange;
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.api.utils.EnchantmentsFilter;

import java.util.Map;

public final class BouTypeTokens {

    public static final TypeToken<CustomLoot> CUSTOM_LOOT = TypeToken.of(CustomLoot.class);
    public static final TypeToken<CustomLoot.Reuse> CUSTOM_LOOT_REUSE = TypeToken.of(CustomLoot.Reuse.class);
    public static final TypeToken<CustomLootCommand> CUSTOM_LOOT_COMMAND = TypeToken.of(CustomLootCommand.class);
    public static final TypeToken<IntQuantity> INT_QUANTITY = TypeToken.of(IntQuantity.class);
    public static final TypeToken<IntRange> INT_RANGE = TypeToken.of(IntRange.class);
    public static final TypeToken<ItemLoot> ITEM_LOOT = TypeToken.of(ItemLoot.class);
    public static final TypeToken<LootReuse> LOOT_REUSE = TypeToken.of(LootReuse.class);
    public static final TypeToken<MoneyLoot> MONEY_LOOT = TypeToken.of(MoneyLoot.class);
    public static final TypeToken<Requirement<?>> REQUIREMENT = new TypeToken<Requirement<?>>() {};
    public static final TypeToken<Map<String, Requirement<?>>> REQUIREMENTS_MAP = new TypeToken<Map<String, Requirement<?>>>() {};
    public static final TypeToken<SpawnedMob> SPAWNED_MOB = TypeToken.of(SpawnedMob.class);
    public static final TypeToken<EnchantmentsFilter> ENCHANTMENTS_FILTER = TypeToken.of(EnchantmentsFilter.class);
    public static final TypeToken<FastHarvestCrop> FAST_HARVEST_CROP = TypeToken.of(FastHarvestCrop.class);
    public static final TypeToken<FastHarvestTools> FAST_HARVEST_TOOLS = TypeToken.of(FastHarvestTools.class);

    public static final TypeToken<Config.BlocksDrops> CONFIG_BLOCKS_DROPS = TypeToken.of(Config.BlocksDrops.class);
    public static final TypeToken<Config.MobsDrops> CONFIG_MOBS_DROPS = TypeToken.of(Config.MobsDrops.class);
    public static final TypeToken<Config.BlockSpawners> CONFIG_BLOCK_SPAWNERS = TypeToken.of(Config.BlockSpawners.class);
    public static final TypeToken<Config.FastHarvest> CONFIG_FAST_HARVEST = TypeToken.of(Config.FastHarvest.class);
    public static final TypeToken<Config.CropsControl> CONFIG_CROPS_CONTROL = TypeToken.of(Config.CropsControl.class);

    private BouTypeTokens() {}
}
