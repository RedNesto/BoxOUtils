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

import io.leangen.geantyref.TypeToken
import org.junit.jupiter.api.fail
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.serialize.TypeSerializerCollection

abstract class ConfigHelper {

    protected abstract fun populateSerializers(builder: TypeSerializerCollection.Builder)

    open fun loadNode(configuration: String): ConfigurationNode {
        val loaderOptions = ConfigurationOptions.defaults()
                .serializers(::populateSerializers)

        val loader = HoconConfigurationLoader.builder()
                .defaultOptions(loaderOptions)
                .source { configuration.reader().buffered() }
                .build()
        return loader.load()
    }

    open fun <N> loadConfig(configuration: String, rootNodeKey: String, token: TypeToken<N>): N {
        val rootNode = loadNode(configuration)
        return rootNode.node(rootNodeKey).get(token) ?: fail("Configuration value is null")
    }

    open fun <N> loadConfigList(configuration: String, rootNodeKey: String, token: TypeToken<N>): List<N> {
        val rootNode = loadNode(configuration)
        return rootNode.node(rootNodeKey).getList(token, emptyList())
    }

    companion object {

        fun create(serializersPopulator: (builder: TypeSerializerCollection.Builder) -> Unit): ConfigHelper {
            return object : ConfigHelper() {
                override fun populateSerializers(builder: TypeSerializerCollection.Builder) = serializersPopulator(builder)
            }
        }
    }
}
