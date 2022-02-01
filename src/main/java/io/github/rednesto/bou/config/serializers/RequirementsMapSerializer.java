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

import io.github.rednesto.bou.api.requirement.Requirement;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RequirementsMapSerializer implements TypeSerializer<Map<String, Requirement>> {

    @Override
    public @Nullable Map<String, Requirement> deserialize(Type type, ConfigurationNode value) throws SerializationException {
        Map<String, Requirement> requirements = new HashMap<>();
        for (ConfigurationNode requirementNode : value.childrenMap().values()) {
            @Nullable Requirement requirement = requirementNode.get(BouTypeTokens.REQUIREMENT);
            if (requirement != null) {
                requirements.put(((String) requirementNode.key()), requirement);
            }
        }

        return requirements;
    }

    @Override
    public void serialize(Type type, @Nullable Map<String, Requirement> obj, ConfigurationNode value) throws SerializationException {
        throw new SerializationException("RequirementsMap cannot be serialized");
    }
}
