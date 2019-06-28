package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.CustomLoot;
import io.github.rednesto.bou.common.lootReuse.LootReuse;
import io.github.rednesto.bou.common.requirement.Requirement;
import io.github.rednesto.bou.sponge.SpongeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLootReuseSerializer implements TypeSerializer<CustomLoot.Reuse> {

    @Override
    public CustomLoot.@Nullable Reuse deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        float multiplier = value.getNode("multiplier").getFloat(1);

        Map<String, LootReuse> reuses = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : value.getNode("items").getChildrenMap().entrySet()) {
            String itemId = SpongeUtils.addMcNamespaceIfNeeded(entry.getKey().toString());
            LootReuse reuse = entry.getValue().getValue(BouTypeTokens.LOOT_REUSE);
            if (reuse != null) {
                reuses.put(itemId, reuse);
            }
        }

        ConfigurationNode requirementsNode = value.getNode("requirements");
        Map<String, Requirement<?>> requirementsMap = requirementsNode.getValue(BouTypeTokens.REQUIREMENTS_MAP);
        List<Requirement<?>> requirements = requirementsMap != null ? new ArrayList<>(requirementsMap.values()) : new ArrayList<>();

        return new CustomLoot.Reuse(multiplier, reuses, requirements);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, CustomLoot.@Nullable Reuse obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
