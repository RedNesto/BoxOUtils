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
