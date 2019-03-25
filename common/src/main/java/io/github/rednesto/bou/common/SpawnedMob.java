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