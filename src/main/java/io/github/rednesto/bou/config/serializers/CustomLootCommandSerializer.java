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
import io.github.rednesto.bou.api.customdrops.CustomLootCommand;
import io.github.rednesto.bou.api.requirement.Requirement;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomLootCommandSerializer extends LintingTypeSerializer<CustomLootCommand> {

    @Override
    public @Nullable CustomLootCommand deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String rawCommand;
        CustomLootCommand.SenderMode senderMode = CustomLootCommand.SenderMode.SERVER;
        double chance = 0;
        List<List<Requirement>> requirements;
        if (value.hasMapChildren()) {
            rawCommand = value.getNode("command").getString();

            ConfigurationNode senderModeNode = value.getNode("as");
            if (!senderModeNode.isVirtual()) {
                String senderModeName = senderModeNode.getString("SENDER");
                try {
                    senderMode = CustomLootCommand.SenderMode.valueOf(senderModeName);
                } catch (IllegalArgumentException e) {
                    fail(senderModeNode, "Sender mode '" + senderModeName + "' is invalid. Possible values: SENDER, PLAYER.");
                }
            }

            chance = value.getNode("chance").getDouble(0);

            ConfigurationNode requirementsNode = value.getNode("requirements");
            requirements = RequirementSerializer.getRequirementGroups(requirementsNode);
        } else {
            rawCommand = value.getString();
            requirements = new ArrayList<>();
        }

        if (rawCommand == null) {
            fail(value, "A command must be specified.");
        }

        return new CustomLootCommand(rawCommand, senderMode, chance, requirements);
    }
}
