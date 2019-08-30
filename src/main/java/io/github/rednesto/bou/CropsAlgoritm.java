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
package io.github.rednesto.bou;

import io.github.rednesto.bou.api.fastharvest.FastHarvestCrop;

import java.util.Random;

public enum CropsAlgoritm {

    ALG_18() {
        @Override
        public int compute(int age, int maxAge, int minimumDrop, int potentialDropCount, int fortune, int fortuneFactor, int chance, int chanceOf) {
            int potentialDrops = potentialDropCount + fortune * fortuneFactor;
            int dropsCount = minimumDrop;

            for (int i = 0; i < potentialDrops; i++) {
                if (chance < 0 || chanceOf < 0) {
                    // we use the Minecraft's chance
                    if (random.nextInt(15) <= age) {
                        dropsCount++;
                    }
                } else {
                    // we use a custom chance
                    if (random.nextInt(chanceOf) <= chance) {
                        dropsCount++;
                    }
                }
            }

            return dropsCount;
        }
    },
    ALG_19() {
        @Override
        public int compute(int age, int maxAge, int minimumDrop, int potentialDropCount, int fortune, int fortuneFactor, int chance, int chanceOf) {
            int potentialDrops = potentialDropCount + fortune * fortuneFactor;
            int dropsCount = minimumDrop;

            for (int i = 0; i < potentialDrops; i++) {
                if (chance < 0 || chanceOf < 0) {
                    // we use the Minecraft's chance
                    if (random.nextInt(maxAge * 2) <= age) {
                        dropsCount++;
                    }
                } else {
                    // we use a custom chance
                    if (random.nextInt(chanceOf) <= chance) {
                        dropsCount++;
                    }
                }
            }

            return dropsCount;
        }
    },
    NETHER_WART() {
        @Override
        public int compute(int age, int maxAge, int minimumDrop, int potentialDropCount, int fortune, int fortuneFactor, int chance, int chanceOf) {
            int dropsCount = minimumDrop + random.nextInt(fortune * fortuneFactor + 1);

            if (chance < 0 || chanceOf < 0) {
                dropsCount += random.nextInt(potentialDropCount + 1);
            } else {
                for (int i = 0; i < potentialDropCount; i++) {
                    if (random.nextInt(chanceOf) <= chance) {
                        dropsCount++;
                    }
                }
            }

            return dropsCount;
        }
    };

    private static Random random = new Random();

    /**
     * Computes the amount of drops for crops, usually seeds (wheat, beetroot), carrots and potatoes
     *
     * @param age the actual age of the crop
     * @param maxAge the maximum age this crops can have, ignored in the 1.8 algorithm
     * @param minimumDrop the amount of drop we are sure to have at the end
     * @param potentialDropCount the number of potential drops
     * @param fortune the level of fortune of the tool used
     * @param fortuneFactor the number which will be multiplied to the fortune param
     * @param chance the chances for having a drop in the total drops, set either this or chanceOf to -1 to disable custom chances
     * @param chanceOf the maximum number wich will be used to compute the chance, exclusive, set either this or chance to -1 to disable custom chances
     *
     * @return the total amount of drops
     */
    public abstract int compute(int age, int maxAge, int minimumDrop, int potentialDropCount, int fortune, int fortuneFactor, int chance, int chanceOf);

    /**
     * Computes the amount of drops for crops, usually seeds (wheat, beetroot), carrots and potatoes
     *
     * @param cropConfig the configuration to use
     * @param age the actual age of the crop
     * @param maxAge the maximum age this crops can have, ignored in the 1.8 algorithm
     * @param fortune the level of fortune of the tool used
     *
     * @return the total amount of drops
     */
    public int compute(FastHarvestCrop cropConfig, int age, int maxAge, int fortune) {
        return compute(age,
                maxAge,
                cropConfig.getMinimum(),
                cropConfig.getCount(),
                fortune,
                cropConfig.getFortuneFactor(),
                cropConfig.getChance(),
                cropConfig.getChanceOf());
    }
}
