package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.MoneyLoot;
import io.github.rednesto.bou.common.quantity.BoundedIntQuantity;
import io.github.rednesto.bou.common.quantity.IIntQuantity;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MoneyLootSerializer implements TypeSerializer<MoneyLoot> {

    @Override
    public @Nullable MoneyLoot deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (value.isVirtual()) {
            return null;
        }

        ConfigurationNode amountNode = value.getNode("amount");
        if (amountNode.isVirtual()) {
            String message = String.format("No money amount set for '%s'. No money will be given for this CustomDrop.", value.getPath()[1]);
            throw new ObjectMappingException(message);
        }

        IIntQuantity quantity = amountNode.getValue(BouTypeTokens.INT_QUANTITY);
        if (quantity == null) {
            return null;
        }

        if (quantity instanceof BoundedIntQuantity) {
            BoundedIntQuantity boundedQuantity = (BoundedIntQuantity) quantity;
            if (boundedQuantity.getFrom() < 0) {
                String message = String.format("The money amount lower bound (%s) for '%s' is negative. This drop will not be loaded.",
                        boundedQuantity.getFrom(), value.getPath()[1]);
                throw new ObjectMappingException(message);
            }

            if (boundedQuantity.getTo() < boundedQuantity.getFrom()) {
                String message = String.format("The quantity upper bound (%s) for '%s' is less than its lower bound (%s). This drop will not be loaded.",
                        boundedQuantity.getTo(), value.getPath()[1], boundedQuantity.getFrom());
                throw new ObjectMappingException(message);
            }
        }

        double chance = 0;
        ConfigurationNode chanceNode = value.getNode("chance");
        if (!chanceNode.isVirtual()) {
            chance = chanceNode.getDouble(Double.NaN);
            if (Double.isNaN(chance)) {
                String message = String.format("The money reward chance of CustomDrop block '%s' is not a valid number ('%s'). This spawn will not be loaded.",
                        value.getPath()[1], chanceNode.getValue());
                throw new ObjectMappingException(message);
            }
        }

        String currency = value.getNode("currency").getString();
        String message = value.getNode("message").getString();
        return new MoneyLoot(quantity, currency, chance, message);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable MoneyLoot obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
