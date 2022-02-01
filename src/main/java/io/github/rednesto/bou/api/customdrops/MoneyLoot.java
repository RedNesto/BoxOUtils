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
package io.github.rednesto.bou.api.customdrops;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;

import java.util.Objects;

public class MoneyLoot {

    private final IntQuantity amount;
    @Nullable
    private final ResourceKey currencyId;
    private final double chance;
    @Nullable
    private final String message;

    public MoneyLoot(IntQuantity amount, @Nullable ResourceKey currencyId, double chance, @Nullable String message) {
        this.amount = amount;
        this.currencyId = currencyId;
        this.chance = chance / 100;
        this.message = message;
    }

    public boolean shouldLoot() {
        return chance <= 0 || Math.random() <= chance;
    }

    public IntQuantity getAmount() {
        return amount;
    }

    @Nullable
    public ResourceKey getCurrencyId() {
        return currencyId;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MoneyLoot)) {
            return false;
        }

        MoneyLoot moneyLoot = (MoneyLoot) o;
        return Double.compare(moneyLoot.chance, chance) == 0 &&
                amount.equals(moneyLoot.amount) &&
                Objects.equals(currencyId, moneyLoot.currencyId) &&
                Objects.equals(message, moneyLoot.message);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("amount", amount)
                .add("currencyId", currencyId)
                .add("chance", chance)
                .add("message", message)
                .toString();
    }
}
