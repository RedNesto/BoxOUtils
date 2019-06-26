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
package io.github.rednesto.bou.common;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.common.quantity.IIntQuantity;

import java.util.Objects;

import javax.annotation.Nullable;

public class ItemLoot {

    private String id;
    @Nullable
    private String providerId;
    @Nullable
    private String displayname;
    private double chance;
    @Nullable
    private IIntQuantity quantity;

    public ItemLoot(String id, @Nullable String providerId, @Nullable String displayname, double chance, @Nullable IIntQuantity quantity) {
        this.id = id;
        this.providerId = providerId;
        this.displayname = displayname;
        this.chance = chance / 100;
        this.quantity = quantity;
    }

    public boolean shouldLoot() {
        return chance <= 0 || Math.random() <= chance;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public String getProviderId() {
        return providerId;
    }

    public double getChance() {
        return chance;
    }

    @Nullable
    public String getDisplayname() {
        return displayname;
    }

    @Nullable
    public IIntQuantity getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemLoot)) {
            return false;
        }

        ItemLoot itemLoot = (ItemLoot) o;
        return Double.compare(itemLoot.chance, chance) == 0 &&
                id.equals(itemLoot.id) &&
                Objects.equals(providerId, itemLoot.providerId) &&
                Objects.equals(displayname, itemLoot.displayname) &&
                Objects.equals(quantity, itemLoot.quantity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("providerId", providerId)
                .add("displayname", displayname)
                .add("chance", chance)
                .add("quantity", quantity)
                .toString();
    }
}
