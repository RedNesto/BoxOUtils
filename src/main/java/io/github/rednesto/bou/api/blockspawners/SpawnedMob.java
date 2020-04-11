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
package io.github.rednesto.bou.api.blockspawners;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.api.quantity.IntQuantity;

import java.util.Objects;

import javax.annotation.Nullable;

public class SpawnedMob {

    private final String id;
    private final double chance;
    @Nullable
    private final IntQuantity quantity;

    public SpawnedMob(String id, double chance, @Nullable IntQuantity quantity) {
        this.id = id;
        this.chance = chance / 100;
        this.quantity = quantity;
    }

    public boolean shouldSpawn() {
        return chance <= 0 || Math.random() <= chance;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public IntQuantity getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpawnedMob)) {
            return false;
        }

        SpawnedMob that = (SpawnedMob) o;
        return Double.compare(that.chance, chance) == 0 &&
                id.equals(that.id) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("chance", chance)
                .add("quantity", quantity)
                .toString();
    }
}
