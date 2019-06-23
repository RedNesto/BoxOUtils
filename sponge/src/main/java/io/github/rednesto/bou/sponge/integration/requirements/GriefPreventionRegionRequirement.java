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
package io.github.rednesto.bou.sponge.integration.requirements;

import io.github.rednesto.bou.common.requirement.CustomLootRequirement;
import io.github.rednesto.bou.common.requirement.CustomLootRequirementProvider;
import io.github.rednesto.bou.common.requirement.RequirementConfigurationException;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.LocatableSnapshot;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GriefPreventionRegionRequirement implements CustomLootRequirement<LocatableSnapshot> {

    private final List<Object> regions;
    private final boolean isWhitelist;

    public GriefPreventionRegionRequirement(List<String> regions, boolean isWhitelist) {
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
    public Class<LocatableSnapshot> getApplicableType() {
        return LocatableSnapshot.class;
    }

    @Override
    public boolean fulfills(LocatableSnapshot source) {
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

    public static class Provider implements CustomLootRequirementProvider {

        @Override
        public String getId() {
            return "griefprevention";
        }

        @Override
        public CustomLootRequirement<?> provide(ConfigurationNode node) throws RequirementConfigurationException {
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

                List<String> regions = regionsNode.getList(TypeTokens.STRING_TOKEN);
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
