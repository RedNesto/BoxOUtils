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
package io.github.rednesto.bou.api.customdrops;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.api.lootReuse.LootReuse;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import io.github.rednesto.bou.api.requirement.Requirement;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class CustomLoot {

    private List<ItemLoot> itemLoots;
    @Nullable
    private IntQuantity experience;
    private boolean overwrite;
    private boolean expOverwrite;
    private List<List<Requirement<?>>> requirements;
    @Nullable
    private MoneyLoot moneyLoot;
    @Nullable
    private Reuse reuse;
    private List<CustomLootCommand> commands;

    public CustomLoot(List<ItemLoot> itemLoots,
                      @Nullable IntQuantity experience,
                      boolean overwrite,
                      boolean expOverwrite,
                      List<List<Requirement<?>>> requirements,
                      @Nullable MoneyLoot moneyLoot,
                      @Nullable Reuse reuse, List<CustomLootCommand> commands) {
        this.itemLoots = itemLoots;
        this.experience = experience;
        this.overwrite = overwrite;
        this.expOverwrite = expOverwrite;
        this.requirements = requirements;
        this.moneyLoot = moneyLoot;
        this.reuse = reuse;
        this.commands = commands;
    }

    public List<ItemLoot> getItemLoots() {
        return itemLoots;
    }

    @Nullable
    public IntQuantity getExperience() {
        return experience;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public boolean isExpOverwrite() {
        return expOverwrite;
    }

    public List<List<Requirement<?>>> getRequirements() {
        return requirements;
    }

    @Nullable
    public MoneyLoot getMoneyLoot() {
        return moneyLoot;
    }

    @Nullable
    public Reuse getReuse() {
        return reuse;
    }

    public List<CustomLootCommand> getCommands() {
        return commands;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomLoot)) {
            return false;
        }

        CustomLoot that = (CustomLoot) o;
        return overwrite == that.overwrite &&
                expOverwrite == that.expOverwrite &&
                itemLoots.equals(that.itemLoots) &&
                Objects.equals(experience, that.experience) &&
                requirements.equals(that.requirements) &&
                Objects.equals(moneyLoot, that.moneyLoot) &&
                Objects.equals(reuse, that.reuse) &&
                commands.equals(that.commands);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("itemLoots", itemLoots)
                .add("experience", experience)
                .add("overwrite", overwrite)
                .add("expOverwrite", expOverwrite)
                .add("requirements", requirements)
                .add("moneyLoot", moneyLoot)
                .add("reuse", reuse)
                .add("commands", commands)
                .toString();
    }

    public static class Reuse {

        private float multiplier;
        private Map<String, LootReuse> items;
        private List<List<Requirement<?>>> requirements;

        public Reuse(float multiplier, Map<String, LootReuse> items, List<List<Requirement<?>>> requirements) {
            this.multiplier = multiplier;
            this.items = items;
            this.requirements = requirements;
        }

        public float getMultiplier() {
            return multiplier;
        }

        public Map<String, LootReuse> getItems() {
            return items;
        }

        public List<List<Requirement<?>>> getRequirements() {
            return requirements;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Reuse)) {
                return false;
            }

            Reuse reuse = (Reuse) o;
            return Float.compare(reuse.multiplier, multiplier) == 0 &&
                    items.equals(reuse.items) &&
                    requirements.equals(reuse.requirements);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("multiplier", multiplier)
                    .add("items", items)
                    .add("requirements", requirements)
                    .toString();
        }
    }
}
