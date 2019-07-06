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

public class FastHarvestCrop {

    private int chance;
    private int chanceOf;
    private int count;
    private int fortuneFactor;
    private int minimum;

    public FastHarvestCrop(int chance, int chanceOf, int count, int fortuneFactor, int minimum) {
        this.chance = chance;
        this.chanceOf = chanceOf;
        this.count = count;
        this.fortuneFactor = fortuneFactor;
        this.minimum = minimum;
    }

    public int getChance() {
        return chance;
    }

    public int getChanceOf() {
        return chanceOf;
    }

    public int getCount() {
        return count;
    }

    public int getFortuneFactor() {
        return fortuneFactor;
    }

    public int getMinimum() {
        return minimum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FastHarvestCrop)) {
            return false;
        }

        FastHarvestCrop that = (FastHarvestCrop) o;
        return chance == that.chance &&
                chanceOf == that.chanceOf &&
                count == that.count &&
                fortuneFactor == that.fortuneFactor &&
                minimum == that.minimum;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("chance", chance)
                .add("chanceOf", chanceOf)
                .add("count", count)
                .add("fortuneFactor", fortuneFactor)
                .add("minimum", minimum)
                .toString();
    }

    public static FastHarvestCrop createDefault() {
        return new FastHarvestCrop(-1, -1, 0, 1, 1);
    }
}
