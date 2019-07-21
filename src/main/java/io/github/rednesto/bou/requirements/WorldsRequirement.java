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
import io.github.rednesto.bou.requirement.AbstractRequirement;
import io.github.rednesto.bou.requirement.Requirement;
import io.github.rednesto.bou.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.requirement.RequirementProvider;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorldsRequirement extends AbstractRequirement<Object> {

    private final List<Object> worlds;

    public WorldsRequirement(List<String> worlds) {
        super("worlds", Object.class);
        this.worlds = worlds.stream().map(id -> {
            try {
                return UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                return id;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public boolean fulfills(Object source, Cause cause) {
        Player player = cause.first(Player.class).orElse(null);
        if (player == null) {
            return true;
        }

        World sourceWorld = player.getWorld();
        for (Object worldId : worlds) {
            if (worldId instanceof UUID) {
                if (sourceWorld.getUniqueId().equals(worldId)) {
                    return true;
                }
            } else {
                if (sourceWorld.getName().equals(worldId)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorldsRequirement)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        WorldsRequirement that = (WorldsRequirement) o;
        return worlds.equals(that.worlds);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("applicableType", getApplicableType())
                .add("worlds", worlds)
                .toString();
    }

    public static class Provider implements RequirementProvider {

        @Override
        public String getId() {
            return "worlds";
        }

        @Override
        public Requirement<?> provide(ConfigurationNode node) throws RequirementConfigurationException {
            try {
                List<String> permissions = node.getList(TypeTokens.STRING_TOKEN);
                return new WorldsRequirement(permissions);
            } catch (ObjectMappingException e) {
                throw new RequirementConfigurationException(e);
            }
        }
    }
}
