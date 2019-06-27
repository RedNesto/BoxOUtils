package io.github.rednesto.bou.common;

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

    public static FastHarvestCrop createDefault() {
        return new FastHarvestCrop(-1, -1, 0, 1, 1);
    }
}
