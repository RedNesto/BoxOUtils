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

import io.github.rednesto.bou.api.customdrops.*;
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.integration.customdrops.recipients.ContextLocationLootRecipient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CustomLootSerializer implements TypeSerializer<CustomLoot> {

    private static final Logger LOGGER = LogManager.getLogger(CustomLootSerializer.class);

    @Nullable
    @Override
    public CustomLoot deserialize(Type type, ConfigurationNode value) throws SerializationException {
        List<CustomDropsProvider> itemLoots = new ArrayList<>(value.node("drops").get(BouTypeTokens.CUSTOM_DROPS_PROVIDER_LIST, Collections.emptyList()));
        itemLoots.removeIf(Objects::isNull);

        ConfigurationNode reuseNode = value.node("reuse");
        CustomLoot.@Nullable Reuse reuse = !reuseNode.virtual() ? reuseNode.get(BouTypeTokens.CUSTOM_LOOT_REUSE) : null;

        ConfigurationNode requirementsNode = value.node("requirements");
        List<List<Requirement>> requirements = RequirementSerializer.getRequirementGroups(requirementsNode);

        boolean overwrite = value.node("overwrite").getBoolean(false);
        boolean expOverwrite = value.node("exp-overwrite").getBoolean(false);

        double chance = 0;
        ConfigurationNode chanceNode = value.node("chance");
        if (!chanceNode.virtual()) {
            chance = chanceNode.getDouble(Double.NaN);
            if (Double.isNaN(chance)) {
                String errorMessage = String.format("Chance value is not a valid number (%s).", chanceNode.raw());
                throw new SerializationException(errorMessage);
            }
        }

        CustomLootRecipient recipient = value.node("recipient").get(BouTypeTokens.CUSTOM_LOOT_RECIPIENT, ContextLocationLootRecipient.INSTANCE);
        boolean redirectBaseDropsToRecipient = value.node("base-drops-to-recipient").getBoolean(true);

        CustomLootComponentProviderIntegrations componentProviderIntegrations = CustomLootComponentProviderIntegrations.getInstance();
        List<CustomLootComponent> components = new ArrayList<>();
        value.childrenMap().forEach((key, node) -> {
            String componentId = (String) key;
            @Nullable CustomLootComponentProvider componentProvider = componentProviderIntegrations.getById(componentId, true);
            if (componentProvider == null) {
                return;
            }

            try {
                components.add(componentProvider.provide(node));
            } catch (Throwable t) {
                LOGGER.error("Unable to read CustomLoot component configuration {}", componentId, t);
            }
        });

        return new CustomLoot(itemLoots, overwrite, expOverwrite, chance, recipient, redirectBaseDropsToRecipient, requirements, reuse, components);
    }

    @Override
    public void serialize(Type type, @Nullable CustomLoot obj, ConfigurationNode value) throws SerializationException {
        throw new SerializationException("CustomLoot cannot be serialized");
    }
}
