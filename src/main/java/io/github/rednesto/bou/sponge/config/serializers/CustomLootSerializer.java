package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.CustomLoot;
import io.github.rednesto.bou.common.ItemLoot;
import io.github.rednesto.bou.common.MoneyLoot;
import io.github.rednesto.bou.common.requirement.Requirement;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomLootSerializer implements TypeSerializer<CustomLoot> {

    @Nullable
    @Override
    public CustomLoot deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        List<ItemLoot> itemLoots = new ArrayList<>(value.getNode("drops").getList(BouTypeTokens.ITEM_LOOT));
        itemLoots.removeIf(Objects::isNull);

        ConfigurationNode reuseNode = value.getNode("reuse");
        CustomLoot.Reuse reuse = !reuseNode.isVirtual() ? reuseNode.getValue(BouTypeTokens.CUSTOM_LOOT_REUSE) : null;

        ConfigurationNode requirementsNode = value.getNode("requirements");
        Map<String, Requirement<?>> requirementsMap = requirementsNode.getValue(BouTypeTokens.REQUIREMENTS_MAP);
        List<Requirement<?>> requirements = requirementsMap != null ? new ArrayList<>(requirementsMap.values()) : new ArrayList<>();

        ConfigurationNode moneyNode = value.getNode("money");
        MoneyLoot moneyLoot = !moneyNode.isVirtual() ? moneyNode.getValue(BouTypeTokens.MONEY_LOOT) : null;

        int experience = value.getNode("experience").getInt();
        boolean overwrite = value.getNode("overwrite").getBoolean(false);
        boolean expOverwrite = value.getNode("exp-overwrite").getBoolean(false);

        return new CustomLoot(itemLoots, experience, overwrite, expOverwrite, requirements, moneyLoot, reuse);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable CustomLoot obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
