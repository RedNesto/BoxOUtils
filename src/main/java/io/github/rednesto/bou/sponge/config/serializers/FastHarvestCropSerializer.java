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
