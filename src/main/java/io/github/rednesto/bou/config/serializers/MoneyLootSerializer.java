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
import io.github.rednesto.bou.api.customdrops.MoneyLoot;
import io.github.rednesto.bou.api.quantity.BoundedIntQuantity;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MoneyLootSerializer extends LintingTypeSerializer<MoneyLoot> {

    @Override
    public @Nullable MoneyLoot deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        ConfigurationNode amountNode = value.getNode("amount");
        IntQuantity quantity = amountNode.getValue(BouTypeTokens.INT_QUANTITY);
        if (quantity == null) {
            fail(amountNode, "Money amount is not set.");
        }

        if (quantity instanceof BoundedIntQuantity) {
            BoundedIntQuantity boundedQuantity = (BoundedIntQuantity) quantity;
            if (boundedQuantity.getFrom() < 0) {
                fail(amountNode, "The money amount lower bound (" + boundedQuantity.getFrom() + ") is negative.");
            }

            if (boundedQuantity.getTo() < boundedQuantity.getFrom()) {
                fail(amountNode, String.format("The quantity upper bound (%s) is less than its lower bound (%s).",
                        boundedQuantity.getTo(), boundedQuantity.getFrom()));
            }
        }

        double chance = 0;
        ConfigurationNode chanceNode = value.getNode("chance");
        if (!chanceNode.isVirtual()) {
            chance = chanceNode.getDouble(Double.NaN);
            if (Double.isNaN(chance)) {
                fail(amountNode, "The money reward chance is not a valid number ('" + chanceNode.getValue() + "').");
            }
        }

        String currency = value.getNode("currency").getString();
        String message = value.getNode("message").getString();
        return new MoneyLoot(quantity, currency, chance, message);
    }
}
