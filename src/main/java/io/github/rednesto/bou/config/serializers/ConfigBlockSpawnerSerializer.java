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
import io.github.rednesto.bou.Config;
import io.github.rednesto.bou.models.SpawnedMob;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigBlockSpawnerSerializer implements TypeSerializer<Config.BlockSpawners> {

    @Override
    public Config.@Nullable BlockSpawners deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        Map<String, List<SpawnedMob>> spawners = new HashMap<>();
        boolean enabled = value.getNode("enabled").getBoolean(false);
        for (Map.Entry<Object, ? extends ConfigurationNode> child : value.getNode("blocks").getChildrenMap().entrySet()) {
            ConfigurationNode node = child.getValue();
            List<SpawnedMob> spawnedMobs = new ArrayList<>(node.getNode("spawns").getList(BouTypeTokens.SPAWNED_MOB));
            spawners.put((String) child.getKey(), spawnedMobs);
        }

        return new Config.BlockSpawners(enabled, spawners);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, Config.@Nullable BlockSpawners obj, @NonNull ConfigurationNode value) {
        throw new UnsupportedOperationException();
    }
}
