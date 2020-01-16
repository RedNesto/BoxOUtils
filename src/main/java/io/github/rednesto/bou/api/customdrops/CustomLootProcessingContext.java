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
package io.github.rednesto.bou.api.customdrops;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class CustomLootProcessingContext {

    private final List<CustomLoot> loots;
    @Nullable
    private final Event event;
    private final Object source;
    private final Cause cause;
    @Nullable
    private final Player targetPlayer;
    @Nullable
    private final Location<World> targetLocation;

    public CustomLootProcessingContext(List<CustomLoot> loots, @Nullable Event event, Object source, Cause cause,
                                       @Nullable Player targetPlayer, @Nullable Location<World> targetLocation) {
        this.loots = Collections.unmodifiableList(loots);
        this.event = event;
        this.source = source;
        this.cause = cause;
        this.targetPlayer = targetPlayer;
        this.targetLocation = targetLocation;
    }

    public List<CustomLoot> getLoots() {
        return loots;
    }

    @Nullable
    public Event getEvent() {
        return event;
    }

    public Object getSource() {
        return source;
    }

    public Cause getCause() {
        return cause;
    }

    @Nullable
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    @Nullable
    public Location<World> getTargetLocation() {
        return targetLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomLootProcessingContext)) {
            return false;
        }

        CustomLootProcessingContext that = (CustomLootProcessingContext) o;
        return loots.containsAll(that.loots) && loots.size() == that.loots.size() &&
                Objects.equals(event, that.event) &&
                source.equals(that.source) &&
                cause.equals(that.cause) &&
                Objects.equals(targetPlayer, that.targetPlayer) &&
                Objects.equals(targetLocation, that.targetLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loots, event, source, cause, targetPlayer, targetLocation);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("loots", loots)
                .add("event", event)
                .add("source", source)
                .add("cause", cause)
                .add("targetPlayer", targetPlayer)
                .add("targetLocation", targetLocation)
                .toString();
    }
}
