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
import io.github.rednesto.bou.common.FastHarvestCrop;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FastHarvestCropSerializer implements TypeSerializer<FastHarvestCrop> {

    @Override
    public @Nullable FastHarvestCrop deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        int chance = value.getNode("chance").getInt(-1);
        int chanceOf = value.getNode("chance_of").getInt(-1);
        int count = value.getNode("count").getInt();
        int fortuneFactor = value.getNode("fortune_factor").getInt(1);
        int minimum = value.getNode("minimum").getInt(1);
        return new FastHarvestCrop(chance, chanceOf, count, fortuneFactor, minimum);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable FastHarvestCrop obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
