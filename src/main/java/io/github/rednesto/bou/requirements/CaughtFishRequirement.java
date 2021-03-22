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
package io.github.rednesto.bou.requirements;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.requirement.AbstractRequirement;
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.api.requirement.RequirementProvider;
import io.github.rednesto.bou.config.linting.LinterContext;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.Fish;
import org.spongepowered.api.data.type.Fishes;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Locale;

public class CaughtFishRequirement extends AbstractRequirement {

    private final FishType fishType;

    public CaughtFishRequirement(FishType fishType) {
        super("caught-fish");
        this.fishType = fishType;
    }

    @Override
    public boolean appliesTo(CustomLootProcessingContext context) {
        return context.getEvent() instanceof FishingEvent.Stop;
    }

    @Override
    public boolean fulfills(CustomLootProcessingContext context) {
        FishingEvent.Stop event = (FishingEvent.Stop) context.getEvent();
        for (Transaction<ItemStackSnapshot> transaction : event.getTransactions()) {
            Fish fish = transaction.getFinal().get(Keys.FISH_TYPE).orElse(null);
            if (fish == null) {
                continue;
            }

            switch (this.fishType) {
                case CLOWNFISH:
                    return fish.equals(Fishes.CLOWNFISH);
                case COD:
                    return fish.equals(Fishes.COD);
                case PUFFERFISH:
                    return fish.equals(Fishes.PUFFERFISH);
                case SALMON:
                    return fish.equals(Fishes.SALMON);
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        CaughtFishRequirement that = (CaughtFishRequirement) o;
        return fishType == that.fishType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("fishType", fishType)
                .toString();
    }

    public enum FishType {
        CLOWNFISH,
        COD,
        PUFFERFISH,
        SALMON
    }

    public static class Provider implements RequirementProvider {

        @Override
        public String getId() {
            return "box-o-utils:caught-fish";
        }

        @Override
        public Requirement provide(ConfigurationNode node) throws ObjectMappingException {
            String id = node.getString();
            if (id == null) {
                LinterContext.fail("Value is not a String", node);
            }

            try {
                FishType fishType = FishType.valueOf(id.toUpperCase(Locale.ROOT));
                return new CaughtFishRequirement(fishType);
            } catch (IllegalArgumentException e) {
                LinterContext.fail("FishType " + id + " is not valid", node);
                return null;
            }
        }
    }
}
