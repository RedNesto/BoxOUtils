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
package io.github.rednesto.bou.integration.universeguard;

import com.google.common.base.MoreObjects;
import com.universeguard.region.Region;
import com.universeguard.utils.RegionUtils;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.requirement.AbstractRequirement;
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.api.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.api.requirement.RequirementProvider;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.LocatableSnapshot;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UniverseGuardRegionRequirement extends AbstractRequirement {

    private final List<Object> regions;
    private final boolean isWhitelist;

    public UniverseGuardRegionRequirement(List<String> regions, boolean isWhitelist) {
        super("universeguard");
        this.regions = regions.stream().map(id -> {
            try {
                return UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                return id;
            }
        }).collect(Collectors.toList());
        this.isWhitelist = isWhitelist;
    }

    @Override
    public boolean appliesTo(CustomLootProcessingContext context) {
        return context.getSource() instanceof LocatableSnapshot;
    }

    @Override
    public boolean fulfills(CustomLootProcessingContext context) {
        LocatableSnapshot<?> source = (LocatableSnapshot<?>) context.getSource();
        if (!source.getLocation().isPresent()) {
            return false;
        }

        Location<World> location = source.getLocation().get();

        Region hereRegion = RegionUtils.getRegion(location);
        for (Object regionId : this.regions) {
            if (regionId instanceof UUID) {
                if (hereRegion.getId().equals(regionId)) {
                    return isWhitelist;
                }
            } else {
                Region regionByName = RegionUtils.load((String) regionId);
                if (hereRegion.getId().equals(regionByName.getId())) {
                    return isWhitelist;
                }
            }
        }

        return !isWhitelist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UniverseGuardRegionRequirement)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        UniverseGuardRegionRequirement that = (UniverseGuardRegionRequirement) o;
        return isWhitelist == that.isWhitelist &&
                regions.equals(that.regions);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("regions", regions)
                .add("isWhitelist", isWhitelist)
                .toString();
    }

    public static class Provider implements RequirementProvider {

        @Override
        public String getId() {
            return "box-o-utils:universeguard";
        }

        @Override
        public Requirement provide(ConfigurationNode node) throws RequirementConfigurationException {
            try {
                String listType = node.getNode("list-type").getString("whitelist");
                boolean isWhitelist;
                if (listType.equalsIgnoreCase("whitelist")) {
                    isWhitelist = true;
                } else if (listType.equalsIgnoreCase("blacklist")) {
                    isWhitelist = false;
                } else {
                    throw new RequirementConfigurationException("Invalid list-type: " + listType);
                }

                ConfigurationNode regionsNode = node.getNode("regions");
                if (regionsNode.isVirtual()) {
                    throw new RequirementConfigurationException("Regions list regions is missing");
                }

                List<String> regions = new ArrayList<>(regionsNode.getList(TypeTokens.STRING_TOKEN));
                if (regions.isEmpty()) {
                    throw new RequirementConfigurationException("Regions list is empty");
                }

                return new UniverseGuardRegionRequirement(regions, isWhitelist);
            } catch (ObjectMappingException e) {
                throw new RequirementConfigurationException(e);
            }
        }
    }
}
