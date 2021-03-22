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
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.api.requirement.RequirementProvider;
import io.github.rednesto.bou.api.requirement.RequirementProviderIntegrations;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequirementSerializer extends LintingTypeSerializer<Requirement> {

    @Override
    public @Nullable Requirement deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        Object rawKey = value.getKey();
        if (!(rawKey instanceof String)) {
            fail(value, "Key is expected to be a String.");
        }

        RequirementProvider requirementProvider = RequirementProviderIntegrations.getInstance().getById((String) rawKey, true);
        if (requirementProvider == null) {
            return null;
        }

        try {
            return requirementProvider.provide(value);
        } catch (Throwable t) {
            fail(value, "Unexpected error during requirement creation", t);
        }

        return null;
    }

    public static List<List<Requirement>> getRequirementGroups(ConfigurationNode requirementsNode) {
        List<Map<String, Requirement>> requirementsMaps = SerializerUtils.getListSafe(requirementsNode, BouTypeTokens.REQUIREMENTS_MAP);
        ArrayList<List<Requirement>> lists = new ArrayList<>();
        for (Map<String, Requirement> requirementsMap : requirementsMaps) {
            ArrayList<Requirement> requirements = new ArrayList<>(requirementsMap.values());
            lists.add(requirements);
        }

        return lists;
    }
}
