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
package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.quantity.BoundedIntQuantity;
import io.github.rednesto.bou.common.quantity.FixedIntQuantity;
import io.github.rednesto.bou.common.quantity.IIntQuantity;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class IntQuantitySerializer implements TypeSerializer<IIntQuantity> {

    @Override
    public @Nullable IIntQuantity deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        int amount = value.getInt();
        // 0 means the value cannot be read as int, so we assume it is a bounded quantity
        if (amount == 0) {
            String bounds = value.getString();
            if (bounds == null) {
                String message = String.format("Invalid bounded amount for '%s'.", value.getPath()[1]);
                throw new ObjectMappingException(message);
            }

            try {
                return BoundedIntQuantity.parse(bounds);
            } catch (NumberFormatException e) {
                String message = String.format("Invalid bounded amount for '%s'.", value.getPath()[1]);
                throw new ObjectMappingException(message);
            }
        } else {
            if (amount < 0) {
                String message = String.format("Invalid fixed amount for '%s'.", value.getPath()[1]);
                throw new ObjectMappingException(message);
            }

            return new FixedIntQuantity(amount);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable IIntQuantity obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
