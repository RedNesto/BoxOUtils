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

import io.github.rednesto.bou.api.fastharvest.FastHarvestTools;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FastHarvestToolsSerializer implements TypeSerializer<FastHarvestTools> {

    @Override
    public @Nullable FastHarvestTools deserialize(Type type, ConfigurationNode value) throws SerializationException {
        boolean enabled = value.node("enabled").getBoolean();
        boolean damageOnUse = value.node("damage_on_use").getBoolean(true);
        boolean isWhitelist = value.node("is_whitelist").getBoolean(true);
        List<String> tools = new ArrayList<>(value.node("tools").getList(String.class, Collections.emptyList()));

        return new FastHarvestTools(enabled, damageOnUse, isWhitelist, tools);
    }

    @Override
    public void serialize(Type type, @Nullable FastHarvestTools obj, ConfigurationNode value) throws SerializationException {
        throw new SerializationException("FastHarvestTools cannot be serialized");
    }
}
