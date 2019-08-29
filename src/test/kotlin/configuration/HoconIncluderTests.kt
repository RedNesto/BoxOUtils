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
package io.github.rednesto.bou.tests.configuration

import com.typesafe.config.ConfigParseOptions
import io.github.rednesto.bou.api.quantity.BoundedIntQuantity
import io.github.rednesto.bou.config.SimpleConfigIncluderFile
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.config.serializers.IntQuantitySerializer
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.nio.file.Paths

class HoconIncluderTests {

    @Test
    fun `include file in same dir`() = doTest("includeFileInSameDir", 1)

    @Test
    fun `include in same dir`() = doTest("includeInSameDir", 1)

    @Test
    fun `include in subdir`() = doTest("includeInSubdir", 2)

    @Test
    fun `include nested`() = doTest("includeNested", 2)

    @Test
    fun `include parent dir`() = doTest("includeParentDir/dir", 1)

    @Test
    fun `include subdir`() = doTest("includeSubdir", 2)

    @Test
    fun `include with custom deserializer`() {
        val serializers = TypeSerializers.newCollection()
                .registerType(BouTypeTokens.INT_QUANTITY, IntQuantitySerializer())
        val node = loadConfig("includeWithCustomDeserializer", serializers)

        val main = node.getNode("main")
        assertFalse(main.isVirtual)
        assertTrue(main.boolean)

        val quantity = node.getNode("quantity")
        assertFalse(quantity.isVirtual)
        assertEquals(BoundedIntQuantity(1, 5), quantity.getValue(BouTypeTokens.INT_QUANTITY))
    }

    private fun doTest(name: String, fileCount: Int) {
        val node = loadConfig(name)

        val main = node.getNode("main")
        assertFalse(main.isVirtual)
        assertTrue(main.boolean)

        for (i in 1..fileCount) {
            val file = node.getNode("file$i")
            assertFalse(main.isVirtual)
            assertEquals(i, file.int)
        }
    }

    private fun loadConfig(name: String, serializers: TypeSerializerCollection = TypeSerializers.getDefaultSerializers()): ConfigurationNode {
        val dirUri = javaClass.getResource("/configurationTests/hoconIncluder/$name") ?: fail { "config directory does not exist" }
        val configDir = Paths.get(dirUri.toURI())

        val options = ConfigurationOptions.defaults().setSerializers(serializers)
        val loader = HoconConfigurationLoader.builder()
                .setParseOptions(ConfigParseOptions.defaults().appendIncluder(SimpleConfigIncluderFile(configDir)))
                .setDefaultOptions(options)
                .setPath(configDir.resolve("main.conf"))
                .build()

        return loader.load()
    }
}
