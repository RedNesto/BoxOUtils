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
package io.github.rednesto.bou.tests

import io.github.rednesto.bou.Config
import io.github.rednesto.bou.api.quantity.BoundedIntQuantity
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.tests.framework.PluginConfigurationTestCase
import io.github.rednesto.bou.tests.framework.mock.id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FishingDropsConfigurationTests : PluginConfigurationTestCase<Config.FishingDrops>("fishingdrops", BouTypeTokens.CONFIG_FISHING_DROPS) {

    @Test
    fun `simple 1`() {
        val config = loadConfig("simple1")

        val drops = listOf(vanillaDrop(id("minecraft:iron_ingot"), quantity = BoundedIntQuantity(0, 3)))
        val expected = listOf(customLoot(drops))

        assertTrue(config.enabled)
        assertEquals(expected, config.loots)
    }
}
