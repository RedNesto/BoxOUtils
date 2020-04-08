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
import io.github.rednesto.bou.api.customdrops.*;
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.integration.customdrops.recipients.ContextLocationLootRecipient;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomLootSerializer implements TypeSerializer<CustomLoot> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLootSerializer.class);

    @Nullable
    @Override
    public CustomLoot deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        List<ItemLoot> itemLoots = new ArrayList<>(value.getNode("drops").getList(BouTypeTokens.ITEM_LOOT));
        itemLoots.removeIf(Objects::isNull);

        ConfigurationNode reuseNode = value.getNode("reuse");
        CustomLoot.Reuse reuse = !reuseNode.isVirtual() ? reuseNode.getValue(BouTypeTokens.CUSTOM_LOOT_REUSE) : null;

        ConfigurationNode requirementsNode = value.getNode("requirements");
        List<List<Requirement<?>>> requirements = RequirementSerializer.getRequirementGroups(requirementsNode);

        boolean overwrite = value.getNode("overwrite").getBoolean(false);
        boolean expOverwrite = value.getNode("exp-overwrite").getBoolean(false);

        CustomLootRecipient recipient = value.getNode("recipient").getValue(BouTypeTokens.CUSTOM_LOOT_RECIPIENT, ContextLocationLootRecipient.INSTANCE);

        CustomLootComponentProviderIntegrations componentProviderIntegrations = CustomLootComponentProviderIntegrations.getInstance();
        List<CustomLootComponent> components = new ArrayList<>();
        value.getChildrenMap().forEach((key, node) -> {
            String componentId = (String) key;
            CustomLootComponentProvider componentProvider = componentProviderIntegrations.getById(componentId, true);
            if (componentProvider == null) {
                return;
            }

            try {
                components.add(componentProvider.provide(node));
            } catch (Throwable t) {
                LOGGER.error("Unable to read CustomLoot component configuration {}", componentId, t);
            }
        });

        return new CustomLoot(itemLoots, overwrite, expOverwrite, recipient, requirements, reuse, components);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable CustomLoot obj, @NonNull ConfigurationNode value) {
        throw new UnsupportedOperationException();
    }
}
