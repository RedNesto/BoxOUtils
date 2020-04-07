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

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import javax.annotation.Nullable;

public class SpongeUtils {

    // The implementation ID is always the same for the running instance
    public static final String SPONGE_IMPL_ID;

    static {
        String spongeImplId;
        try {
            spongeImplId = Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getId();
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
        ConfigurationNode parent = node.getParent();
        for (int i = 1; i < nth; i++) {
            if (parent == null) {
                return null;
            }

            parent = parent.getParent();
        }

        return parent;
    }

    public static Location<World> getCenteredLocation(BlockSnapshot snapshot, World world) {
        return center(snapshot.getLocation().orElseGet(() -> new Location<>(world, snapshot.getPosition())));
    }

    public static <E extends Extent> Location<E> center(Location<E> location) {
        return location.add(0.5, 0.5, 0.5);
    }
}
