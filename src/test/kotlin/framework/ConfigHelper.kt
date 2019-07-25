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

abstract class ConfigHelper {

    protected abstract fun populateSerializers(serializers: TypeSerializerCollection)

    open fun loadNode(configuration: String): ConfigurationNode {
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

    open fun <N> loadConfig(configuration: String, rootNodeKey: String, token: TypeToken<N>): N {
        val rootNode = loadNode(configuration)
        return rootNode.getNode(rootNodeKey).getValue(token) ?: fail("Configuration value is null")
    }

    companion object {

        fun create(serializersPopulator: (serializers: TypeSerializerCollection) -> Unit): ConfigHelper {
            return object : ConfigHelper() {
                override fun populateSerializers(serializers: TypeSerializerCollection) = serializersPopulator(serializers)
            }
        }
    }
}
