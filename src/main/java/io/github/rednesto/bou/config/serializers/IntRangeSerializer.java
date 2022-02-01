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

import io.github.rednesto.bou.api.range.BoundedIntRange;
import io.github.rednesto.bou.api.range.FixedIntRange;
import io.github.rednesto.bou.api.range.IntRange;
import io.github.rednesto.bou.api.range.SelectiveIntRange;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class IntRangeSerializer implements TypeSerializer<IntRange> {

    @Override
    public @Nullable IntRange deserialize(Type type, ConfigurationNode value) throws SerializationException {
        @Nullable String stringValue = value.getString();
        if (stringValue == null) {
            throw new SerializationException(value, String.class, "Could not read value");
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

        throw new SerializationException(value, String.class, "Invalid integer range '" + stringValue + "'.");
    }

    @Override
    public void serialize(Type type, @Nullable IntRange obj, ConfigurationNode value) throws SerializationException {
        throw new SerializationException("IntRange cannot be serialized");
    }
}
