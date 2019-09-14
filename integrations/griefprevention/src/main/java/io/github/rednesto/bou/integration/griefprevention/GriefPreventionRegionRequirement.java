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
package io.github.rednesto.bou.integration.griefprevention;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.api.requirement.AbstractRequirement;
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.api.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.api.requirement.RequirementProvider;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.LocatableSnapshot;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GriefPreventionRegionRequirement extends AbstractRequirement<LocatableSnapshot> {

    private final List<Object> regions;
    private final boolean isWhitelist;

    public GriefPreventionRegionRequirement(List<String> regions, boolean isWhitelist) {
        super("griefprevention", LocatableSnapshot.class);
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
    public boolean fulfills(LocatableSnapshot source, Cause cause) {
        if (!source.getLocation().isPresent()) {
            return false;
        }

        Location<World> location = (Location<World>) source.getLocation().get();
        ClaimManager claimManager = GriefPrevention.getApi().getClaimManager(location.getExtent());
        Claim hereClaim = claimManager.getClaimAt(location);
        for (Object regionId : this.regions) {
            if (regionId instanceof UUID) {
                if (hereClaim.getUniqueId().equals(regionId)) {
                    return isWhitelist;
                }
            } else {
                List<Claim> claimsByName = claimManager.getClaimsByName((String) regionId);
                for (Claim claim : claimsByName) {
                    if (hereClaim.getUniqueId().equals(claim.getUniqueId())) {
                        return isWhitelist;
                    }
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
        if (!(o instanceof GriefPreventionRegionRequirement)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        GriefPreventionRegionRequirement that = (GriefPreventionRegionRequirement) o;
        return isWhitelist == that.isWhitelist &&
                regions.equals(that.regions);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("applicableType", getApplicableType())
                .add("regions", regions)
                .add("isWhitelist", isWhitelist)
                .toString();
    }

    public static class Provider implements RequirementProvider {

        @Override
        public String getId() {
            return "box-o-utils:griefprevention";
        }

        @Override
        public Requirement<?> provide(ConfigurationNode node) throws RequirementConfigurationException {
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

                return new GriefPreventionRegionRequirement(regions, isWhitelist);
            } catch (ObjectMappingException e) {
                throw new RequirementConfigurationException(e);
            }
        }
    }
}
