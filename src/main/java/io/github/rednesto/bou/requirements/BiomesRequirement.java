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

import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.requirement.AbstractRequirement;
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.api.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.api.requirement.RequirementProvider;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.LocatableSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.biome.Biome;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.Collections;

public class BiomesRequirement extends AbstractRequirement {

    private static final Marker logMarker = MarkerManager.getMarker("BiomesRequirement");
    private final Collection<ResourceKey> acceptedBiomes;

    public BiomesRequirement(Collection<ResourceKey> acceptedBiomes) {
        super("biomes");
        this.acceptedBiomes = acceptedBiomes;
    }

    @Override
    public boolean fulfills(CustomLootProcessingContext context) {
        Object source = context.getSource();
        @MonotonicNonNull Location<?, ?> location;
        if (source instanceof LocatableSnapshot) {
            location = ((LocatableSnapshot<?>) source).location().orElse(null);
        } else if (source instanceof Locatable) {
            location = ((Locatable) source).location();
        } else {
            BoxOUtils.getInstance().getLogger().warn(logMarker, "Invalid source passed to fulfills: {}.", source);
            return true;
        }

        if (location == null) {
            BoxOUtils.getInstance().getLogger().debug(logMarker, "Could not find location for source {}.", source);
            return true;
        }

        Biome biome = location.biome();
        if (biome == null) {
            BoxOUtils.getInstance().getLogger().debug(logMarker, "Could not find biome for source {}.", source);
            return true;
        }

        ResourceKey biomeKey = RegistryTypes.BIOME.keyFor(location.world(), biome);
        return acceptedBiomes.contains(biomeKey);
    }

    @Override
    public boolean appliesTo(CustomLootProcessingContext context) {
        return context.getSource() instanceof LocatableSnapshot || context.getSource() instanceof Locatable;
    }

    public static class Provider implements RequirementProvider {

        @Override
        public Requirement provide(ConfigurationNode node) throws RequirementConfigurationException {
            try {
                return new BiomesRequirement(node.getList(ResourceKey.class, Collections.emptyList()));
            } catch (SerializationException e) {
                throw new RequirementConfigurationException("Could not read list of biomes IDs", e);
            }
        }

        @Override
        public String getId() {
            return "box-o-utils:biomes";
        }
    }
}
