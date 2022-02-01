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

import io.github.rednesto.bou.api.quantity.BoundedIntQuantity
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.config.serializers.IntQuantitySerializer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.nio.file.Paths

@Disabled("The includer is not yet usable with Configurate 4")
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
        val serializers = TypeSerializerCollection.defaults().childBuilder()
                .register(BouTypeTokens.INT_QUANTITY, IntQuantitySerializer())
                .build()
        val node = loadConfig("includeWithCustomDeserializer", serializers)

        val main = node.node("main")
        assertFalse(main.virtual())
        assertTrue(main.boolean)

        val quantity = node.node("quantity")
        assertFalse(quantity.virtual())
        assertEquals(BoundedIntQuantity(1, 5), quantity.get(BouTypeTokens.INT_QUANTITY))
    }

    private fun doTest(name: String, fileCount: Int) {
        val node = loadConfig(name)

        val main = node.node("main")
        assertFalse(main.virtual())
        assertTrue(main.boolean)

        for (i in 1..fileCount) {
            val file = node.node("file$i")
            assertFalse(main.virtual())
            assertEquals(i, file.int)
        }
    }

    private fun loadConfig(name: String, serializers: TypeSerializerCollection = TypeSerializerCollection.defaults()): ConfigurationNode {
        val dirUri = javaClass.getResource("/configurationTests/hoconIncluder/$name") ?: fail { "config directory does not exist" }
        val configDir = Paths.get(dirUri.toURI())

        val options = ConfigurationOptions.defaults().serializers(serializers)
        val loader = HoconConfigurationLoader.builder()
                //.setParseOptions(ConfigParseOptions.defaults().appendIncluder(SimpleConfigIncluderFile(configDir)))
                .defaultOptions(options)
                .path(configDir.resolve("main.conf"))
                .build()

        return loader.load()
    }
}
