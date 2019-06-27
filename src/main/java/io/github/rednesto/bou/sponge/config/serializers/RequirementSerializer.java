package io.github.rednesto.bou.sponge.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.common.requirement.Requirement;
import io.github.rednesto.bou.common.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.common.requirement.RequirementProvider;
import io.github.rednesto.bou.sponge.IntegrationsManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RequirementSerializer implements TypeSerializer<Requirement<?>> {

    @Override
    public @Nullable Requirement<?> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String key = (String) value.getKey();
        RequirementProvider requirementProvider = IntegrationsManager.INSTANCE.getRequirementProvider(key);
        if (requirementProvider == null) {
            return null;
        }

        try {
            return requirementProvider.provide(value);
        } catch (RequirementConfigurationException e) {
            throw new ObjectMappingException("Unable to read requirement", e);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Requirement<?> obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
