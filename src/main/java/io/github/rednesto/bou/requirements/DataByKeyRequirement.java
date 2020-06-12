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
import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.requirement.AbstractRequirement;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataByKeyRequirement<C extends ValueContainer<C>> extends AbstractRequirement {

    private final Class<C> applicableType;
    private final Map<String, List<Object>> requiredData;

    public DataByKeyRequirement(String id, Class<C> applicableType, Map<String, List<Object>> requiredData) {
        super(id);
        this.applicableType = applicableType;
        this.requiredData = requiredData;
    }

    @Override
    public boolean appliesTo(CustomLootProcessingContext context) {
        return requiredData.getClass().isAssignableFrom(context.getSource().getClass());
    }

    @Override
    public boolean fulfills(CustomLootProcessingContext context) {
        for (Map.Entry<String, List<Object>> entry : this.requiredData.entrySet()) {
            String keyId = entry.getKey();
            List<Object> expectedValues = entry.getValue();

            //noinspection rawtypes
            Optional<Key> maybeDataKey = Sponge.getRegistry().getType(Key.class, keyId);
            if (!maybeDataKey.isPresent()) {
                BoxOUtils.getInstance().getLogger().warn("Could not find a data key for id '{}'", keyId);
                continue;
            }

            // We use raw types because we have no idea what type the compared values are
            //noinspection rawtypes
            Key dataKey = maybeDataKey.get();
            Object dataValue = null;
            //noinspection unchecked
            C source = (C) context.getSource();
            if (source.supports(dataKey)) {
                //noinspection unchecked
                dataValue = source.getOrNull(dataKey);
            } else if (source instanceof BlockSnapshot) {
                // Workaround to access BlockState data. Unfortunately, it is too late to query TileEntity data.
                Location<World> location = ((BlockSnapshot) source).getLocation().orElse(null);
                if (location != null && location.supports(dataKey)) {
                    //noinspection unchecked
                    dataValue = location.getOrNull(dataKey);
                }
            }

            if (dataValue == null) {
                BoxOUtils.getInstance().getLogger().warn("Data container does not support '{}'", keyId);
                continue;
            }

            if (dataValue instanceof CatalogType) {
                return expectedValues.contains(((CatalogType) dataValue).getId());
            } else {
                return expectedValues.contains(dataValue);
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataByKeyRequirement)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        DataByKeyRequirement<?> that = (DataByKeyRequirement<?>) o;
        return applicableType.equals(that.applicableType)
                && requiredData.equals(that.requiredData);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("applicableType", this.applicableType)
                .add("requiredData", requiredData)
                .toString();
    }
}
