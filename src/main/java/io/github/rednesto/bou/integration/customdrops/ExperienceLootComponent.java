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
package io.github.rednesto.bou.integration.customdrops;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.api.customdrops.CustomLootComponent;
import io.github.rednesto.bou.api.customdrops.CustomLootComponentConfigurationException;
import io.github.rednesto.bou.api.customdrops.CustomLootComponentProvider;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import io.github.rednesto.bou.config.serializers.BouTypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ExperienceLootComponent implements CustomLootComponent {

    private final IntQuantity experienceQuantity;

    public ExperienceLootComponent(IntQuantity experienceQuantity) {
        this.experienceQuantity = experienceQuantity;
    }

    @Override
    public void processLoot(CustomLootProcessingContext processingContext) {
        Location<World> targetLocation = processingContext.getTargetLocation();
        if (targetLocation == null) {
            return;
        }

        int experienceAmount = experienceQuantity.get();
        if (experienceAmount > 0) {
            Entity experienceOrb = targetLocation.createEntity(EntityTypes.EXPERIENCE_ORB);
            experienceOrb.offer(Keys.CONTAINED_EXPERIENCE, experienceAmount);
            targetLocation.spawnEntity(experienceOrb);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExperienceLootComponent)) {
            return false;
        }

        ExperienceLootComponent that = (ExperienceLootComponent) o;
        return experienceQuantity.equals(that.experienceQuantity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("experienceQuantity", experienceQuantity)
                .toString();
    }

    public static class Provider implements CustomLootComponentProvider {

        @Override
        public CustomLootComponent provide(ConfigurationNode node) throws CustomLootComponentConfigurationException {
            try {
                IntQuantity experience = node.getValue(BouTypeTokens.INT_QUANTITY);
                if (experience == null) {
                    throw new CustomLootComponentConfigurationException("Could not deserialize a MoneyLoot.");
                }

                return new ExperienceLootComponent(experience);
            } catch (ObjectMappingException e) {
                throw new CustomLootComponentConfigurationException(e);
            }
        }

        @Override
        public String getId() {
            return "box-o-utils:experience";
        }
    }
}
