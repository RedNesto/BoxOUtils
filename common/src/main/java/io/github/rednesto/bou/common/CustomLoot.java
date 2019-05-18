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

import io.github.rednesto.bou.common.lootReuse.LootReuse;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CustomLoot {

    private List<ItemLoot> itemLoots;
    private int experience;
    private boolean overwrite;
    private boolean expOverwrite;
    @Nullable
    private MoneyLoot moneyLoot;
    @Nullable
    private Reuse reuse;

    public CustomLoot(List<ItemLoot> itemLoots, int experience, boolean overwrite, boolean expOverwrite, @Nullable MoneyLoot moneyLoot, @Nullable Reuse reuse) {
        this.itemLoots = itemLoots;
        this.experience = experience;
        this.overwrite = overwrite;
        this.expOverwrite = expOverwrite;
        this.moneyLoot = moneyLoot;
        this.reuse = reuse;
    }

    public List<ItemLoot> getItemLoots() {
        return itemLoots;
    }

    public int getExperience() {
        return experience;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public boolean isExpOverwrite() {
        return expOverwrite;
    }

    @Nullable
    public MoneyLoot getMoneyLoot() {
        return moneyLoot;
    }

    @Nullable
    public Reuse getReuse() {
        return reuse;
    }

    public static class Reuse {

        private float multiplier;
        private Map<String, LootReuse> items;

        public Reuse(float multiplier, Map<String, LootReuse> items) {
            this.multiplier = multiplier;
            this.items = items;
        }

        public float getMultiplier() {
            return multiplier;
        }

        public Map<String, LootReuse> getItems() {
            return items;
        }
    }
}
