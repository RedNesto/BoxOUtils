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
import io.github.rednesto.bou.api.quantity.FixedIntQuantity
import io.github.rednesto.bou.api.quantity.IntQuantity
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.config.serializers.IntQuantitySerializer
import io.github.rednesto.bou.tests.framework.ConfigurationTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializerCollection

class IntQuantityTests : ConfigurationTestCase<IntQuantity>("quantity", BouTypeTokens.INT_QUANTITY) {

    @Test
    fun `fixed quantity`() = assertEquals(loadConfig("quantity=1"), FixedIntQuantity(1))

    @Test
    fun `bounded quantity`() = assertEquals(loadConfig("quantity=2-10"), BoundedIntQuantity(2, 10))

    @Test
    fun `invalid quantity`() {
        val thrown = assertThrows<SerializationException> { loadConfig("quantity=invalid") }
        assertTrue(thrown.cause is IllegalArgumentException)
    }

    @Test
    fun `invalid bounded quantity`() {
        val thrown = assertThrows<SerializationException> { loadConfig("quantity=2-a3") }
        assertTrue(thrown.cause is NumberFormatException)
    }

    override fun populateSerializers(builder: TypeSerializerCollection.Builder) {
        builder.register(BouTypeTokens.INT_QUANTITY, IntQuantitySerializer())
    }
}
