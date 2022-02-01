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

import io.github.rednesto.bou.api.quantity.BoundedIntQuantity;
import io.github.rednesto.bou.api.quantity.FixedIntQuantity;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class IntQuantitySerializer implements TypeSerializer<IntQuantity> {

    @Override
    public @Nullable IntQuantity deserialize(Type type, ConfigurationNode value) throws SerializationException {
        @Nullable String stringValue = value.getString();
        if (stringValue == null) {
            throw new SerializationException(value, String.class, "Could not read value");
        }

        try {
            return new FixedIntQuantity(Integer.parseInt(stringValue));
        } catch (NumberFormatException ignore) {
            // The value is not a simple int, so it should be a bounded quantity
        }

        try {
            return BoundedIntQuantity.parse(stringValue);
        } catch (IllegalArgumentException e) {
            throw new SerializationException(value, String.class, "Invalid amount.", e);
        }
    }

    @Override
    public void serialize(Type type, @Nullable IntQuantity obj, ConfigurationNode value) throws SerializationException {
        throw new SerializationException("IntQuantity cannot be serialized");
    }
}
