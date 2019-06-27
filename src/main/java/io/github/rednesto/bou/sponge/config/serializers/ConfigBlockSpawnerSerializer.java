package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.SpawnedMob;
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
    public void serialize(@NonNull TypeToken<?> type, Config.@Nullable BlockSpawners obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
