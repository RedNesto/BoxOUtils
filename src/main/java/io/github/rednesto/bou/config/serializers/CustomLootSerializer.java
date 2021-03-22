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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CustomLootSerializer extends LintingTypeSerializer<CustomLoot> {

    @Nullable
    @Override
    public CustomLoot deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        List<CustomDropsProvider> itemLoots = new ArrayList<>(value.getNode("drops").getValue(BouTypeTokens.CUSTOM_DROPS_PROVIDER_LIST, Collections.emptyList()));
        itemLoots.removeIf(Objects::isNull);

        CustomLoot.Reuse reuse = value.getNode("reuse").getValue(BouTypeTokens.CUSTOM_LOOT_REUSE);
        List<List<Requirement>> requirements = RequirementSerializer.getRequirementGroups(value.getNode("requirements"));
        boolean overwrite = value.getNode("overwrite").getBoolean(false);
        boolean expOverwrite = value.getNode("exp-overwrite").getBoolean(false);

        double chance = 0;
        ConfigurationNode chanceNode = value.getNode("chance");
        if (!chanceNode.isVirtual()) {
            chance = chanceNode.getDouble(Double.NaN);
            if (Double.isNaN(chance)) {
                fail(chanceNode, "Chance value is not a valid number (" + chanceNode.getValue() + ").");
            }
        }

        CustomLootRecipient recipient = value.getNode("recipient").getValue(BouTypeTokens.CUSTOM_LOOT_RECIPIENT, ContextLocationLootRecipient.INSTANCE);
        boolean redirectBaseDropsToRecipient = value.getNode("base-drops-to-recipient").getBoolean(true);

        CustomLootComponentProviderIntegrations componentProviderIntegrations = CustomLootComponentProviderIntegrations.getInstance();
        List<CustomLootComponent> components = new ArrayList<>();
        value.getChildrenMap().forEach((key, node) -> {
            String componentId = (String) key;
            CustomLootComponentProvider componentProvider = componentProviderIntegrations.getById(componentId, true);
            if (componentProvider != null) {
                try {
                    components.add(componentProvider.provide(node));
                } catch (Throwable t) {
                    error(value, "Loot component '" + componentId + "' unexpected error: " + t.getMessage());
                }
            }
        });

        return new CustomLoot(itemLoots, overwrite, expOverwrite, chance, recipient, redirectBaseDropsToRecipient, requirements, reuse, components);
    }
}
