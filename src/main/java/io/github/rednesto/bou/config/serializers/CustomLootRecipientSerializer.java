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

import io.github.rednesto.bou.api.customdrops.CustomLootRecipient;
import io.github.rednesto.bou.api.customdrops.CustomLootRecipientProvider;
import io.github.rednesto.bou.api.customdrops.CustomLootRecipientProviderIntegrations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class CustomLootRecipientSerializer implements TypeSerializer<CustomLootRecipient> {

    private static final Logger LOGGER = LogManager.getLogger(CustomLootRecipientSerializer.class);

    @Override
    public @Nullable CustomLootRecipient deserialize(Type type, ConfigurationNode value) {
        @Nullable String providerId = value.getString();
        @Nullable ConfigurationNode recipientConfigNode = null;
        if (providerId == null) {
            providerId = value.node("id").getString();
            recipientConfigNode = value;
            if (providerId == null) {
                return null;
            }
        }

        CustomLootRecipientProviderIntegrations recipientProviderIntegrations = CustomLootRecipientProviderIntegrations.getInstance();
        @Nullable CustomLootRecipientProvider provider = recipientProviderIntegrations.getById(providerId, true);
        if (provider == null){
            return null;
        }

        try {
            return provider.provide(recipientConfigNode);
        } catch (Throwable t) {
            LOGGER.error("Unable to read CustomLootRecipient configuration of provider {}", providerId, t);
        }
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable CustomLootRecipient obj, ConfigurationNode value) throws SerializationException {
        //throw new SerializationException("CustomLootRecipient cannot be serialized");
    }
}
