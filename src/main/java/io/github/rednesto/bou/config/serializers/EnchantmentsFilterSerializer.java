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

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.SpongeUtils;
import io.github.rednesto.bou.api.range.IntRange;
import io.github.rednesto.bou.api.utils.EnchantmentsFilter;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentsFilterSerializer implements TypeSerializer<EnchantmentsFilter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantmentsFilterSerializer.class);

    private static final List<String> ANY_VALUES = Arrays.asList("any", "all");
    private static final List<String> NONE_VALUES = Arrays.asList("none", "disallow");

    @Override
    public @Nullable EnchantmentsFilter deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) {
        if (!value.hasMapChildren()) {
            return null;
        }

        Map<String, IntRange> neededRanges = new HashMap<>();
        Map<String, IntRange> disallowedRanges = new HashMap<>();
        Map<String, Boolean> wildcards = new HashMap<>();
        for (ConfigurationNode filterNode : value.getChildrenMap().values()) {
            String key = (String) filterNode.getKey();
            if (key == null) {
                LOGGER.warn("An enchantements filter item configuration is missing its key!");
                continue;
            }

            key = SpongeUtils.addMcNamespaceIfNeeded(key);

            ConfigurationNode disallowNode = filterNode.getNode("disallow");
            if (!disallowNode.isVirtual()) {
                try {
                    IntRange range = disallowNode.getValue(BouTypeTokens.INT_RANGE);
                    if (range != null) {
                        disallowedRanges.put(key, range);
                    }
                } catch (ObjectMappingException e) {
                    LOGGER.error("Error reading enchantment requirement filter.", e);
                }

                continue;
            }

            String stringValue = filterNode.getString();
            if (stringValue != null) {
                if (NONE_VALUES.contains(stringValue)) {
                    wildcards.put(key, false);
                    continue;
                } else if (ANY_VALUES.contains(stringValue)) {
                    wildcards.put(key, true);
                    continue;
                }
            }

            try {
                IntRange range = filterNode.getValue(BouTypeTokens.INT_RANGE);
                if (range != null) {
                    neededRanges.put(key, range);
                }
            } catch (ObjectMappingException e) {
                LOGGER.error("Error reading enchantment requirement filter.", e);
            }
        }

        return new EnchantmentsFilter(neededRanges, disallowedRanges, wildcards);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable EnchantmentsFilter obj, @NonNull ConfigurationNode value) {
        throw new UnsupportedOperationException();
    }
}
