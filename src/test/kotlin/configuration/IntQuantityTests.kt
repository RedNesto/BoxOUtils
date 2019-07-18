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

import io.github.rednesto.bou.common.quantity.BoundedIntQuantity
import io.github.rednesto.bou.common.quantity.FixedIntQuantity
import io.github.rednesto.bou.common.quantity.IntQuantity
import io.github.rednesto.bou.sponge.config.serializers.BouTypeTokens
import io.github.rednesto.bou.sponge.config.serializers.IntQuantitySerializer
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IntQuantityTests {

    @Test
    fun `fixed quantity`() = assertEquals(load("quantity=1"), FixedIntQuantity(1))

    @Test
    fun `bounded quantity`() = assertEquals(load("quantity=2-10"), BoundedIntQuantity(2, 10))

    @Test
    fun `invalid quantity`() {
        val thrown = assertThrows<ObjectMappingException> { load("quantity=invalid") }
        assertTrue(thrown.cause is IllegalArgumentException)
    }

    @Test
    fun `invalid bounded quantity`() {
        val thrown = assertThrows<ObjectMappingException> { load("quantity=2-a3") }
        assertTrue(thrown.cause is NumberFormatException)
    }

    private val loaderOptions = ConfigurationOptions.defaults()
            .setSerializers(TypeSerializers.newCollection().registerType(BouTypeTokens.INT_QUANTITY, IntQuantitySerializer()))

    private fun load(configuration: String): IntQuantity? {
        val loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(loaderOptions)
                .setSource { configuration.reader().buffered() }
                .build()

        val rootNode = loader.load()
        return rootNode.getNode("quantity").getValue(BouTypeTokens.INT_QUANTITY)
    }
}
