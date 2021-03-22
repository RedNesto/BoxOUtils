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
import io.github.rednesto.bou.config.linting.LinterContext;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

public abstract class LintingTypeSerializer<T> implements TypeSerializer<T> {

    @Override
    public @Nullable T deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support deserialization.");
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable T obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support serialization.");
    }

    protected final void warn(ConfigurationNode node, String message) {
        LinterContext.registerWarning(message, node);
    }

    protected final void error(ConfigurationNode node, String message) {
        LinterContext.registerError(message, node);
    }

    protected final void error(ConfigurationNode node, String message, @Nullable Throwable cause) {
        LinterContext.registerError(message, node, cause);
    }

    @Contract("_, _ -> fail")
    protected final void fail(ConfigurationNode node, String message) throws ObjectMappingException {
        LinterContext.fail(message, node);
    }

    @Contract("_, _, _ -> fail")
    protected final void fail(ConfigurationNode node, String message, @Nullable Throwable cause) throws ObjectMappingException {
        LinterContext.fail(message, node, cause);
    }
}
