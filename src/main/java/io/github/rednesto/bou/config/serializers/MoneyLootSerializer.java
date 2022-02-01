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

import io.github.rednesto.bou.api.customdrops.MoneyLoot;
import io.github.rednesto.bou.api.quantity.BoundedIntQuantity;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class MoneyLootSerializer implements TypeSerializer<MoneyLoot> {

    @Override
    public @Nullable MoneyLoot deserialize(Type type, ConfigurationNode value) throws SerializationException {
        if (value.virtual()) {
            return null;
        }

        ConfigurationNode amountNode = value.node("amount");
        if (amountNode.virtual()) {
            String message = String.format("No money amount set for '%s'. No money will be given for this CustomDrop.", value.parent().key());
            throw new SerializationException(value.parent(), MoneyLoot.class, message);
        }

        @Nullable IntQuantity quantity = amountNode.get(BouTypeTokens.INT_QUANTITY);
        if (quantity == null) {
            return null;
        }

        if (quantity instanceof BoundedIntQuantity) {
            BoundedIntQuantity boundedQuantity = (BoundedIntQuantity) quantity;
            if (boundedQuantity.getFrom() < 0) {
                String message = String.format("The money amount lower bound (%s) for '%s' is negative. This drop will not be loaded.",
                        boundedQuantity.getFrom(), value.parent().key());
                throw new SerializationException(amountNode, BoundedIntQuantity.class, message);
            }

            if (boundedQuantity.getTo() < boundedQuantity.getFrom()) {
                String message = String.format("The quantity upper bound (%s) for '%s' is less than its lower bound (%s). This drop will not be loaded.",
                        boundedQuantity.getTo(), value.parent().key(), boundedQuantity.getFrom());
                throw new SerializationException(amountNode, BoundedIntQuantity.class, message);
            }
        }

        double chance = 0;
        ConfigurationNode chanceNode = value.node("chance");
        if (!chanceNode.virtual()) {
            chance = chanceNode.getDouble(Double.NaN);
            if (Double.isNaN(chance)) {
                String message = String.format("The money reward chance of CustomDrop block '%s' is not a valid number ('%s'). This spawn will not be loaded.",
                        value.parent().key(), chanceNode.raw());
                throw new SerializationException(chanceNode, double.class, message);
            }
        }

        @Nullable ResourceKey currency = value.node("currency").get(ResourceKey.class);
        @Nullable String message = value.node("message").getString();
        return new MoneyLoot(quantity, currency, chance, message);
    }

    @Override
    public void serialize(Type type, @Nullable MoneyLoot obj, ConfigurationNode value) throws SerializationException {
        throw new SerializationException("MoneyLoot cannot be serialized");
    }
}
