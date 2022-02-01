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

import io.github.rednesto.bou.api.customdrops.CustomLootCommand;
import io.github.rednesto.bou.api.requirement.Requirement;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CustomLootCommandSerializer implements TypeSerializer<CustomLootCommand> {

    @Override
    public @Nullable CustomLootCommand deserialize(Type type, ConfigurationNode value) throws SerializationException {
        @Nullable String rawCommand;
        CustomLootCommand.SenderMode senderMode = CustomLootCommand.SenderMode.SERVER;
        double chance = 0;
        List<List<Requirement>> requirements;
        if (value.isMap()) {
            rawCommand = value.node("command").getString();

            ConfigurationNode senderModeNode = value.node("as");
            if (!senderModeNode.virtual()) {
                String senderModeName = senderModeNode.getString("SENDER");
                try {
                    senderMode = CustomLootCommand.SenderMode.valueOf(senderModeName);
                } catch (IllegalArgumentException e) {
                    String errorMessage = String.format("Sender mode '%s' is invalid. Possible values: SENDER, PLAYER.", senderModeName);
                    throw new SerializationException(errorMessage);
                }
            }

            chance = value.node("chance").getDouble(0);

            ConfigurationNode requirementsNode = value.node("requirements");
            requirements = RequirementSerializer.getRequirementGroups(requirementsNode);
        } else {
            rawCommand = value.getString();
            requirements = new ArrayList<>();
        }

        if (rawCommand == null) {
            throw new SerializationException("A command must be specified.");
        }

        return new CustomLootCommand(rawCommand, senderMode, chance, requirements);
    }

    @Override
    public void serialize(Type type, @Nullable CustomLootCommand obj, ConfigurationNode value) throws SerializationException {
        throw new SerializationException("CustomLootCommand cannot be serialized");
    }
}
