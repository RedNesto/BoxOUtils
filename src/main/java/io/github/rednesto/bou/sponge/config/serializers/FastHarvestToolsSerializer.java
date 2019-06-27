package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.FastHarvestTools;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.util.TypeTokens;

import java.util.ArrayList;
import java.util.List;

public class FastHarvestToolsSerializer implements TypeSerializer<FastHarvestTools> {

    @Override
    public @Nullable FastHarvestTools deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        boolean enabled = value.getNode("enabled").getBoolean();
        boolean isWhitelist = value.getNode("is_whitelist").getBoolean();
        List<String> tools = new ArrayList<>(value.getNode("tools").getList(TypeTokens.STRING_TOKEN));
        return new FastHarvestTools(enabled, isWhitelist, tools);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable FastHarvestTools obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
