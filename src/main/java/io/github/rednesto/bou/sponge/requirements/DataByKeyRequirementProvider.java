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
package io.github.rednesto.bou.sponge.requirements;

import io.github.rednesto.bou.common.requirement.Requirement;
import io.github.rednesto.bou.common.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.common.requirement.RequirementProvider;
import io.github.rednesto.bou.sponge.SpongeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.util.TypeTokens;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataByKeyRequirementProvider<C extends ValueContainer<C>> implements RequirementProvider {

    private final String id;
    private final Class<C> requirementType;

    public DataByKeyRequirementProvider(String id, Class<C> valueContainerType) {
        this.id = id;
        this.requirementType = valueContainerType;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Requirement<?> provide(ConfigurationNode node) throws RequirementConfigurationException {
        if (!node.hasMapChildren()) {
            throw new RequirementConfigurationException("A data requirement does not have any data keys to check");
        }

        Map<String, List<String>> requiredData = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> pair : node.getChildrenMap().entrySet()) {
            String keyId = pair.getKey().toString();
            ConfigurationNode valueNode = pair.getValue();

            String expandedId = SpongeUtils.addSpongeImplNamespaceIfNeeded(keyId);
            if (valueNode.hasListChildren()) {
                try {
                    requiredData.put(expandedId, valueNode.getList(TypeTokens.STRING_TOKEN));
                } catch (ObjectMappingException e) {
                    throw new RequirementConfigurationException(e);
                }
            } else {
                requiredData.put(expandedId, Collections.singletonList(valueNode.getString()));
            }
        }


        return new DataByKeyRequirement<>(this.requirementType, requiredData);
    }
}
