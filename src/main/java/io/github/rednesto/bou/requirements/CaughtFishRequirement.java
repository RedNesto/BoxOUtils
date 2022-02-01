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
import io.github.rednesto.bou.api.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.api.requirement.RequirementProvider;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Objects;

public class CaughtFishRequirement extends AbstractRequirement {

    private final ResourceKey fishType;

    public CaughtFishRequirement(ResourceKey fishType) {
        super("caught-fish");
        this.fishType = fishType;
    }

    @Override
    public boolean appliesTo(CustomLootProcessingContext context) {
        return context.getEvent() instanceof FishingEvent.Stop;
    }

    @Override
    public boolean fulfills(CustomLootProcessingContext context) {
        FishingEvent.Stop event = (FishingEvent.Stop) Objects.requireNonNull(context.getEvent());
        for (Transaction<ItemStackSnapshot> transaction : event.transactions()) {
            ItemType itemType = transaction.finalReplacement().type();
            ResourceKey itemTypeId = RegistryTypes.ITEM_TYPE.keyFor(Sponge.game(), itemType);
            if (this.fishType.equals(itemTypeId)) {
                return true;
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

    public static class Provider implements RequirementProvider {

        @Override
        public String getId() {
            return "box-o-utils:caught-fish";
        }

        @Override
        public Requirement provide(ConfigurationNode node) throws RequirementConfigurationException {
            try {
                @Nullable ResourceKey id = node.get(ResourceKey.class);
                if (id == null) {
                    throw new RequirementConfigurationException("Fish type must be set");
                }
                return new CaughtFishRequirement(id);
            } catch (SerializationException e) {
                throw new RequirementConfigurationException("Fish type is not valid", e);
            }
        }
    }
}
