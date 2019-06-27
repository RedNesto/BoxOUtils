package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.*;
import io.github.rednesto.bou.common.lootReuse.LootReuse;
import io.github.rednesto.bou.common.quantity.IIntQuantity;
import io.github.rednesto.bou.common.requirement.Requirement;

import java.util.Map;

public final class BouTypeTokens {

    public static final TypeToken<CustomLoot> CUSTOM_LOOT = TypeToken.of(CustomLoot.class);
    public static final TypeToken<CustomLoot.Reuse> CUSTOM_LOOT_REUSE = TypeToken.of(CustomLoot.Reuse.class);
    public static final TypeToken<IIntQuantity> INT_QUANTITY = TypeToken.of(IIntQuantity.class);
    public static final TypeToken<ItemLoot> ITEM_LOOT = TypeToken.of(ItemLoot.class);
    public static final TypeToken<LootReuse> LOOT_REUSE = TypeToken.of(LootReuse.class);
    public static final TypeToken<MoneyLoot> MONEY_LOOT = TypeToken.of(MoneyLoot.class);
    public static final TypeToken<Requirement<?>> REQUIREMENT = new TypeToken<Requirement<?>>() {};
    public static final TypeToken<Map<String, Requirement<?>>> REQUIREMENTS_MAP = new TypeToken<Map<String, Requirement<?>>>() {};
    public static final TypeToken<SpawnedMob> SPAWNED_MOB = TypeToken.of(SpawnedMob.class);
    public static final TypeToken<FastHarvestCrop> FAST_HARVEST_CROP = TypeToken.of(FastHarvestCrop.class);
    public static final TypeToken<FastHarvestTools> FAST_HARVEST_TOOLS = TypeToken.of(FastHarvestTools.class);

    public static final TypeToken<Config.BlocksDrops> CONFIG_BLOCKS_DROPS = TypeToken.of(Config.BlocksDrops.class);
    public static final TypeToken<Config.MobsDrops> CONFIG_MOBS_DROPS = TypeToken.of(Config.MobsDrops.class);
    public static final TypeToken<Config.BlockSpawners> CONFIG_BLOCK_SPAWNERS = TypeToken.of(Config.BlockSpawners.class);
    public static final TypeToken<Config.FastHarvest> CONFIG_FAST_HARVEST = TypeToken.of(Config.FastHarvest.class);

    private BouTypeTokens() {}
}
