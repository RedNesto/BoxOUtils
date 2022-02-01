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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.registry.RegistryKey;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DataByKeyRequirement<C extends ValueContainer> extends AbstractRequirement {

    private final Class<C> applicableType;
    private final Map<String, List<Object>> requiredData;
    private final Function<CustomLootProcessingContext, Object> containerSelector;

    public DataByKeyRequirement(String id, Class<C> applicableType, Map<String, List<Object>> requiredData) {
        this(id, applicableType, requiredData, CustomLootProcessingContext::getSource);
    }

    public DataByKeyRequirement(String id, Class<C> applicableType, Map<String, List<Object>> requiredData, Function<CustomLootProcessingContext, Object> containerSelector) {
        super(id);
        this.applicableType = applicableType;
        this.requiredData = requiredData;
        this.containerSelector = containerSelector;
    }

    @Override
    public boolean appliesTo(CustomLootProcessingContext context) {
        return this.applicableType.isAssignableFrom(this.containerSelector.apply(context).getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean fulfills(CustomLootProcessingContext context) {
        for (Map.Entry<String, List<Object>> entry : this.requiredData.entrySet()) {
            String keyId = entry.getKey();
            ResourceKey resKeyId = ResourceKey.resolve(keyId);
            List<Object> expectedValues = entry.getValue();

            C source = (C) this.containerSelector.apply(context);

            @Nullable Key<Value<Object>> dataKey = null;
            try {
                dataKey = searchForKey(resKeyId, source);
            } catch (IllegalStateException ignore) {
                // ValueContainer.getKeys() might fail due to multiple data sources being registered for the same key
                // So let's try a bit harder whenever that happens
                if (source instanceof EntitySnapshot) {
                    Optional<Entity> restoredEntity = ((EntitySnapshot) source).restore();
                    if (restoredEntity.isPresent()) {
                        dataKey = searchForKey(resKeyId, restoredEntity.get());
                    }
                }
            }

            if (dataKey == null) {
                // Fallback in case we can't use getKeys() for some reason
                // Far from perfect but oh well ¯\_(ツ)_/¯
                String fieldName = resKeyId.value().toUpperCase(Locale.ROOT);
                try {
                    dataKey = (Key<Value<Object>>) Keys.class.getField(fieldName).get(null);
                } catch (IllegalAccessException | NoSuchFieldException ignored) {
                }
            }

            if (dataKey == null) {
                BoxOUtils.getInstance().getLogger().warn("Could not find key '{}' in source {}", keyId, source);
                continue;
            }

            // We use raw types because we have no idea what type the compared values are
            @Nullable Object dataValue = null;
            if (source.supports(dataKey)) {
                dataValue = source.getOrNull(dataKey);
            } else if (source instanceof BlockSnapshot) {
                // Workaround to access BlockState data. Unfortunately, it is too late to query TileEntity data.
                @Nullable ServerLocation location = ((BlockSnapshot) source).location().orElse(null);
                if (location != null && location.supports(dataKey)) {
                    dataValue = location.getOrNull(dataKey);
                }
            } else if (source instanceof EntitySnapshot) {
                // Workaround to access Entity data.
                @Nullable Entity entity = ((EntitySnapshot) source).restore().orElse(null);
                if (entity != null && entity.supports(dataKey)) {
                    dataValue = entity.getOrNull(dataKey);
                }
            } else {
                BoxOUtils.getInstance().getLogger().warn("Data container does not support '{}'", keyId);
                continue;
            }

            if (dataValue instanceof ResourceKey) {
                return expectedValues.contains(((ResourceKey) dataValue).formatted());
            } else if (dataValue instanceof RegistryKey) {
                return expectedValues.contains(((RegistryKey<?>) dataKey).location().formatted());
            } else if (dataValue instanceof Component) {
                return expectedValues.contains(PlainTextComponentSerializer.plainText().serialize((Component) dataValue));
            } else {
                return expectedValues.contains(dataValue);
            }
        }

        return true;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Key<Value<Object>> searchForKey(ResourceKey resKeyId, ValueContainer source) {
        for (Key<?> key : source.getKeys()) {
            if (key.key().equals(resKeyId)) {
                return (Key<Value<Object>>) key;
            }
        }
        return null;
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
