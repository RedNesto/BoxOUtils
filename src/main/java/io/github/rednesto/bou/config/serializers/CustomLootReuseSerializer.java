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

import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.lootReuse.LootReuse;
import io.github.rednesto.bou.api.requirement.Requirement;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLootReuseSerializer implements TypeSerializer<CustomLoot.Reuse> {

    @Override
    public CustomLoot.@Nullable Reuse deserialize(Type type, ConfigurationNode value) throws SerializationException {
        float multiplier = value.node("multiplier").getFloat(1);

        Map<ResourceKey, LootReuse> reuses = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : value.node("items").childrenMap().entrySet()) {
            ResourceKey itemId = ResourceKey.resolve(entry.getKey().toString());
            @Nullable LootReuse reuse = entry.getValue().get(BouTypeTokens.LOOT_REUSE);
            if (reuse != null) {
                reuses.put(itemId, reuse);
            }
        }

        ConfigurationNode requirementsNode = value.node("requirements");
        List<List<Requirement>> requirements = RequirementSerializer.getRequirementGroups(requirementsNode);

        return new CustomLoot.Reuse(multiplier, reuses, requirements);
    }

    @Override
    public void serialize(Type type, CustomLoot.@Nullable Reuse obj, ConfigurationNode value) throws SerializationException {
        throw new SerializationException("CustomLootResult cannot be serialized");
    }
}
