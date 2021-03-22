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
import io.github.rednesto.bou.api.range.BoundedIntRange;
import io.github.rednesto.bou.api.range.FixedIntRange;
import io.github.rednesto.bou.api.range.IntRange;
import io.github.rednesto.bou.api.range.SelectiveIntRange;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class IntRangeSerializer extends LintingTypeSerializer<IntRange> {

    @Override
    public @Nullable IntRange deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String stringValue = value.getString();
        if (stringValue == null) {
            fail(value, "Could not read value.");
        }

        try {
            return new FixedIntRange(Integer.parseInt(stringValue));
        } catch (NumberFormatException ignore) {
        }

        try {
            return BoundedIntRange.parse(stringValue);
        } catch (IllegalArgumentException ignored) {
        }

        try {
            return SelectiveIntRange.parse(stringValue);
        } catch (IllegalArgumentException ignored) {
        }

        fail(value, "Invalid integer range '" + stringValue + "'.");
        return null;
    }
}
