package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.lootReuse.LootReuse;
import io.github.rednesto.bou.common.lootReuse.MultiplyLootReuse;
import io.github.rednesto.bou.common.lootReuse.SimpleLootReuse;
import io.github.rednesto.bou.common.quantity.IIntQuantity;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LootReuseSerializer implements TypeSerializer<LootReuse> {

    @Override
    public @Nullable LootReuse deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        ConfigurationNode multiplierNode = value.getNode("multiplier");
        if (!multiplierNode.isVirtual()) {
            return new MultiplyLootReuse(multiplierNode.getFloat());
        }

        ConfigurationNode quantityNode = value.getNode("quantity");
        if (!quantityNode.isVirtual()) {
            IIntQuantity quantity = quantityNode.getValue(BouTypeTokens.INT_QUANTITY);
            if (quantity == null) {
                return null;
            }

            return new SimpleLootReuse(quantity);
        }

        return null;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable LootReuse obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
