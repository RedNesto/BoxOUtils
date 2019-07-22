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
package io.github.rednesto.bou.tests.framework

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.junit.jupiter.api.fail

abstract class ConfigurationTestCase<T>(val rootNodeKey: String, val typeToken: TypeToken<T>) {

    protected abstract fun populateSerializers(serializers: TypeSerializerCollection)

    protected open fun loadNode(configuration: String): ConfigurationNode {
        val typeSerializers = TypeSerializers.newCollection()
        populateSerializers(typeSerializers)

        val loaderOptions = ConfigurationOptions.defaults()
                .setSerializers(typeSerializers)

        val loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(loaderOptions)
                .setSource { configuration.reader().buffered() }
                .build()
        return loader.load()
    }

    protected open fun loadConfig(configuration: String): T {
        val rootNode = loadNode(configuration)
        return rootNode.getNode(rootNodeKey).getValue(typeToken) ?: fail("Configuration value is null")
    }
}
