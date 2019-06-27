package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.SpawnedMob;
import io.github.rednesto.bou.common.quantity.BoundedIntQuantity;
import io.github.rednesto.bou.common.quantity.IIntQuantity;
import io.github.rednesto.bou.sponge.SpongeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SpawnedMobSerializer implements TypeSerializer<SpawnedMob> {

    @Override
    public @Nullable SpawnedMob deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String mobType = value.getNode("type").getString();
        if (mobType == null) {
            String message = String.format("A BlockSpawner spawn for '%s' has no 'type'", getBlockId(value));
            throw new ObjectMappingException(message);
        }

        IIntQuantity quantity = null;
        ConfigurationNode quantityNode = value.getNode("quantity");
        if (!quantityNode.isVirtual()) {
            quantity = quantityNode.getValue(BouTypeTokens.INT_QUANTITY);
        }

        if (quantity instanceof BoundedIntQuantity) {
            BoundedIntQuantity boundedQuantity = (BoundedIntQuantity) quantity;
            if (boundedQuantity.getFrom() < 0) {
                String message = String.format("The quantity lower bound (%s) of BlockSpawner '%s' for mob '%s' is negative. This spawn will not be loaded.",
                        boundedQuantity.getFrom(), mobType, getBlockId(value));
                throw new ObjectMappingException(message);
            }

            if (boundedQuantity.getTo() < boundedQuantity.getFrom()) {
                String message = String.format("The quantity upper bound (%s) of BlockSpawner '%s' for mob '%s' is less than its lower bound (%s). This spawn will not be loaded.",
                        boundedQuantity.getTo(), mobType, getBlockId(value), boundedQuantity.getFrom());
                throw new ObjectMappingException(message);
            }
        }

        double chance = 0;
        ConfigurationNode chanceNode = value.getNode("chance");
        if (!chanceNode.isVirtual()) {
            chance = chanceNode.getDouble(Double.NaN);
            if (Double.isNaN(chance)) {
                String message = String.format("Chance of BlockSpawner mob '%s' for block '%s' is not a valid number ('%s'). This spawn will not be loaded.",
                        mobType, getBlockId(value), chanceNode.getValue());
                throw new ObjectMappingException(message);
            }
        }

        return new SpawnedMob(mobType, chance, quantity);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable SpawnedMob obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public String getBlockId(@NonNull ConfigurationNode value) {
        ConfigurationNode blockNode = SpongeUtils.getNthParent(value, 2);
        if (blockNode == null) {
            return null;
        }

        return (String) blockNode.getKey();
    }
}
