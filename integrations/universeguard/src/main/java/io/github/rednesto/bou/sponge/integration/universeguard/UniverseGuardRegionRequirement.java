package io.github.rednesto.bou.sponge.integration.universeguard;

import com.universeguard.region.Region;
import com.universeguard.utils.RegionUtils;
import io.github.rednesto.bou.common.requirement.Requirement;
import io.github.rednesto.bou.common.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.common.requirement.RequirementProvider;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.LocatableSnapshot;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UniverseGuardRegionRequirement implements Requirement<LocatableSnapshot> {

    private final List<Object> regions;
    private final boolean isWhitelist;

    public UniverseGuardRegionRequirement(List<String> regions, boolean isWhitelist) {
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

    public static class Provider implements RequirementProvider {

        @Override
        public String getId() {
            return "universeguard";
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

                List<String> regions = regionsNode.getList(TypeTokens.STRING_TOKEN);
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
