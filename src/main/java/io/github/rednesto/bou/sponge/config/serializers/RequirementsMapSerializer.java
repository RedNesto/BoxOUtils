package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.requirement.Requirement;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RequirementsMapSerializer implements TypeSerializer<Map<String, Requirement<?>>> {

    @Override
    public @Nullable Map<String, Requirement<?>> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        Map<String, Requirement<?>> requirements = new HashMap<>();
        for (ConfigurationNode requirementNode : value.getChildrenMap().values()) {
            Requirement<?> requirement = requirementNode.getValue(BouTypeTokens.REQUIREMENT);
            if (requirement != null) {
                requirements.put(((String) requirementNode.getKey()), requirement);
            }
        }

        return requirements;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Map<String, Requirement<?>> obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
