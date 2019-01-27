package io.github.rednesto.bou.common;

import java.util.Random;

public class MoneyLoot {

    private BoundedIntQuantity amount;
    private int chance;
    private String message;

    private static final Random RANDOM = new Random();

    public MoneyLoot(BoundedIntQuantity amount, int chance, String message) {
        this.amount = amount;
        this.chance = chance;
        this.message = message;
    }

    public boolean shouldLoot() {
        return chance <= 0 || RANDOM.nextInt(100) <= chance;
    }

    public BoundedIntQuantity getAmount() {
        return amount;
    }

    public int getChance() {
        return chance;
    }

    public String getMessage() {
        return message;
    }
}
