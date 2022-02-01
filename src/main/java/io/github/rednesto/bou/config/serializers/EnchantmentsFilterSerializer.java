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
package io.github.rednesto.bou.config.serializers;

import io.github.rednesto.bou.api.range.IntRange;
import io.github.rednesto.bou.api.utils.EnchantmentsFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentsFilterSerializer implements TypeSerializer<EnchantmentsFilter> {

    private static final Logger LOGGER = LogManager.getLogger(EnchantmentsFilterSerializer.class);

    private static final List<String> ANY_VALUES = Arrays.asList("any", "all");
    private static final List<String> NONE_VALUES = Arrays.asList("none", "disallow");

    @Override
    public @Nullable EnchantmentsFilter deserialize(Type type, ConfigurationNode value) {
        if (!value.isMap()) {
            return null;
        }

        Map<ResourceKey, IntRange> neededRanges = new HashMap<>();
        Map<ResourceKey, IntRange> disallowedRanges = new HashMap<>();
        Map<ResourceKey, Boolean> wildcards = new HashMap<>();
        for (ConfigurationNode filterNode : value.childrenMap().values()) {
            @Nullable String key = (String) filterNode.key();
            if (key == null) {
                LOGGER.warn("An enchantements filter item configuration is missing its key!");
                continue;
            }

            ConfigurationNode disallowNode = filterNode.node("disallow");
            if (!disallowNode.virtual()) {
                try {
                    @Nullable IntRange range = disallowNode.get(BouTypeTokens.INT_RANGE);
                    if (range != null) {
                        disallowedRanges.put(ResourceKey.resolve(key), range);
                    }
                } catch (SerializationException e) {
                    LOGGER.error("Error reading enchantment requirement filter.", e);
                }

                continue;
            }

            @Nullable String stringValue = filterNode.getString();
            if (stringValue != null) {
                if (NONE_VALUES.contains(stringValue)) {
                    wildcards.put(ResourceKey.resolve(key), false);
                    continue;
                } else if (ANY_VALUES.contains(stringValue)) {
                    wildcards.put(ResourceKey.resolve(key), true);
                    continue;
                }
            }

            try {
                @Nullable IntRange range = filterNode.get(BouTypeTokens.INT_RANGE);
                if (range != null) {
                    neededRanges.put(ResourceKey.resolve(key), range);
                }
            } catch (SerializationException e) {
                LOGGER.error("Error reading enchantment requirement filter.", e);
            }
        }

        return new EnchantmentsFilter(neededRanges, disallowedRanges, wildcards);
    }

    @Override
    public void serialize(Type type, @Nullable EnchantmentsFilter obj, ConfigurationNode value) throws SerializationException {
        throw new SerializationException("EnchantmentFilter cannot be serialized");
    }
}
