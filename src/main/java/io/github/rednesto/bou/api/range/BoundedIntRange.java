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
package io.github.rednesto.bou.api.range;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class BoundedIntRange implements IntRange {

    private int min;
    private int max;

    public BoundedIntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isInRange(int value) {
        return value >= this.min && value <= this.max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoundedIntRange)) {
            return false;
        }

        BoundedIntRange that = (BoundedIntRange) o;
        return min == that.min &&
                max == that.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("min", min)
                .add("max", max)
                .toString();
    }

    public static BoundedIntRange parse(String toParse) {
        String[] bounds = toParse.split("-", 2);
        if (bounds.length != 2) {
            throw new IllegalArgumentException();
        }

        int min = Integer.parseInt(bounds[0]);
        int max = Integer.parseInt(bounds[1]);
        return new BoundedIntRange(min, max);
    }
}
