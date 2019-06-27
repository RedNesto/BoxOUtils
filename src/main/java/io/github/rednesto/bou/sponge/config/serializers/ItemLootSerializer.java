package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.ItemLoot;
import io.github.rednesto.bou.common.quantity.BoundedIntQuantity;
import io.github.rednesto.bou.common.quantity.IIntQuantity;
import io.github.rednesto.bou.sponge.BoxOUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ItemLootSerializer implements TypeSerializer<ItemLoot> {

    @Nullable
    @Override
    public ItemLoot deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String providerId;
        String itemId;
        if (value.getNode("file_inv_id").isVirtual()) {
            providerId = value.getNode("provider").getString();
            itemId = value.getNode("type").getString();
        } else {
            // TODO Remove this branch in a future update. Only exists for backwards compatibility
            providerId = "file-inv";
            itemId = value.getNode("file_inv_id").getString();
            BoxOUtils plugin = BoxOUtils.getInstance();
            plugin.getLogger().warn("The CustomDrop for '" + value.getParent().getKey() + "' uses the 'file_inv_id' property which will be removed in a future version.");
            plugin.getLogger().warn("Please replace this key with 'type' and add 'provider = \"file-inv\"' beside it.");
        }

        if (itemId == null) {
            throw new ObjectMappingException("The CustomDrop for '" + value.getParent().getKey() + "' does not have a 'type'. It will not be loaded.");
        }

        ConfigurationNode quantityNode = value.getNode("quantity");
        IIntQuantity quantity = !quantityNode.isVirtual() ? quantityNode.getValue(BouTypeTokens.INT_QUANTITY) : null;

        if (quantity instanceof BoundedIntQuantity) {
            BoundedIntQuantity boundedQuantity = (BoundedIntQuantity) quantity;
            if (boundedQuantity.getFrom() < 0) {
                String errorMessage = String.format("The quantity lower bound (%s) of CustomDrop '%s' for '%s' is negative. This drop will not be loaded.",
                        boundedQuantity.getFrom(), itemId, value.getParent().getKey());
                throw new ObjectMappingException(errorMessage);
            }

            if (boundedQuantity.getTo() < boundedQuantity.getFrom()) {
                String errorMessage = String.format("The quantity upper bound (%s) of CustomDrop '%s' for '%s' is less than its lower bound (%s). This drop will not be loaded.",
                        boundedQuantity.getTo(), itemId, value.getParent().getKey(), boundedQuantity.getFrom());
                throw new ObjectMappingException(errorMessage);
            }
        }

        double chance = 0;
        ConfigurationNode chanceNode = value.getNode("chance");
        if (!chanceNode.isVirtual()) {
            chance = chanceNode.getDouble(Double.NaN);
            if (Double.isNaN(chance)) {
                String errorMessage = String.format("Chance of CustomDrop '%s' of block '%s' is not a valid number ('%s'). This spawn will not be loaded.",
                        itemId, value.getParent().getKey(), chanceNode.getValue());
                throw new ObjectMappingException(errorMessage);
            }
        }

        return new ItemLoot(itemId, providerId, value.getNode("displayname").getString(), chance, quantity);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable ItemLoot obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
