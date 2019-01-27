package io.github.rednesto.bou.common;

import java.util.Random;

public class BoundedIntQuantity {

    private int from;
    private int to;

    private static final Random RANDOM = new Random();

    public BoundedIntQuantity(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getRandomQuantity() {
        if(from == to)
            return to;

        return RANDOM.nextInt(to + 1 - from) + from;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public static BoundedIntQuantity parse(String toParse) {
        String[] bounds = toParse.split("-", 2);
        int from = Integer.parseInt(bounds[0]);
        int to = Integer.parseInt(bounds[1]);
        return new BoundedIntQuantity(from, to);
    }
}
