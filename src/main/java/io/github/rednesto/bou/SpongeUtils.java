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
package io.github.rednesto.bou;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;

public class SpongeUtils {

    // The implementation ID is always the same for the running instance
    public static final String SPONGE_IMPL_ID;

    static {
        String spongeImplId;
        try {
            spongeImplId = Sponge.platform().container(Platform.Component.IMPLEMENTATION).metadata().id();
        } catch (IllegalStateException e) {
            // ISE is thrown if Sponge is not initialized, like when unit-testing.
            // But bou.is_testing is also set for integration tests,
            // so we have to check for it only when an ISE is thrown here
            if (BouUtils.isTesting()) {
                spongeImplId = "sponge_impl";
            } else {
                throw e;
            }
        }

        SPONGE_IMPL_ID = spongeImplId;
    }

    public static String addMcNamespaceIfNeeded(String id) {
        return addNamespaceIfNeeded(id, "minecraft");
    }

    public static String addSpongeImplNamespaceIfNeeded(String id) {
        return addNamespaceIfNeeded(id, SPONGE_IMPL_ID);
    }

    public static String addNamespaceIfNeeded(String id, String namespace) {
        if (!id.contains(":")) {
            return namespace + ":" + id;
        }

        return id;
    }

    @Nullable
    public static String getModId(String id) {
        String[] parts = id.split(":", 2);
        if (parts.length >= 2) {
            return parts[0];
        }
        return null;
    }

    @Nullable
    public static ConfigurationNode getNthParent(ConfigurationNode node, int nth) {
        @Nullable ConfigurationNode parent = node.parent();
        for (int i = 1; i < nth; i++) {
            if (parent == null) {
                return null;
            }

            parent = parent.parent();
        }

        return parent;
    }

    public static ServerLocation getCenteredLocation(BlockSnapshot snapshot, ServerWorld world) {
        return center(snapshot.location().orElseGet(() -> world.location(snapshot.position())));
    }

    public static ServerLocation center(ServerLocation location) {
        return location.add(0.5, 0.5, 0.5);
    }

    public static void spawnExpOrbs(ServerLocation location, int amount) {
        List<Entity> orbs = new ArrayList<>();
        int remaining = amount;
        while (remaining > 0) {
            int split = getXPSplit(remaining);
            remaining -= split;
            Entity orb = location.createEntity(EntityTypes.EXPERIENCE_ORB.get());
            orb.offer(Keys.EXPERIENCE, split);
            orbs.add(orb);
        }

        Task spawnOrbsTask = Task.builder()
                .delay(Ticks.of(1))
                .execute(() -> location.spawnEntities(orbs))
                .plugin(BoxOUtils.getInstance().getContainer())
                .build();
        Sponge.server().scheduler().submit(spawnOrbsTask, "Spawn experience orbs @" + location.position() + " in " + location.worldKey().formatted());
    }

    public static int getXPSplit(int expValue) {
        // Taken from MCP's EntityXPOrb
        if (expValue >= 2477) {
            return 2477;
        } else if (expValue >= 1237) {
            return 1237;
        } else if (expValue >= 617) {
            return 617;
        } else if (expValue >= 307) {
            return 307;
        } else if (expValue >= 149) {
            return 149;
        } else if (expValue >= 73) {
            return 73;
        } else if (expValue >= 37) {
            return 37;
        } else if (expValue >= 17) {
            return 17;
        } else if (expValue >= 7) {
            return 7;
        } else {
            return expValue >= 3 ? 3 : 1;
        }
    }
}
