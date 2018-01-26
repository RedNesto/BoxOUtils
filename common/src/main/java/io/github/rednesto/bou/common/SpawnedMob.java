package io.github.rednesto.bou.common;

import java.util.Random;

public class SpawnedMob {

    private String id;
    private int chance;
    private int quantityFrom;
    private int quantityTo;

    private static final Random random = new Random();

    public SpawnedMob(String id, int chance, int quantityFrom, int quantityTo) {
        this.id = id;
        this.chance = chance;
        this.quantityFrom = quantityFrom;
        this.quantityTo = quantityTo;
    }

    public boolean shouldSpawn() {
        return chance <= 0 || random.nextInt(100) <= chance;
    }

    public int getQuantityToSpawn() {
        if(quantityFrom == quantityTo)
            return quantityTo;

        return random.nextInt(quantityTo + 1 - quantityFrom) + quantityFrom;
    }

    public String getId() {
        return id;
    }

    public int getChance() {
        return chance;
    }

    public int getQuantityTo() {
        return quantityTo;
    }
}
