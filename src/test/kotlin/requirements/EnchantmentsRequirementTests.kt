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
package io.github.rednesto.bou.tests.requirements

import io.github.rednesto.bou.api.range.BoundedIntRange
import io.github.rednesto.bou.api.range.FixedIntRange
import io.github.rednesto.bou.api.range.SelectiveIntRange
import io.github.rednesto.bou.api.requirement.Requirement
import io.github.rednesto.bou.api.utils.enchantmentsFilter.EnchantmentsFilter
import io.github.rednesto.bou.api.utils.enchantmentsFilter.SimpleEnchantmentsFilter
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.config.serializers.EnchantmentsFilterSerializer
import io.github.rednesto.bou.config.serializers.IntRangeSerializer
import io.github.rednesto.bou.requirements.EnchantmentsRequirement
import io.github.rednesto.bou.tests.framework.ConfigHelper
import io.github.rednesto.bou.tests.framework.mock.MockEnchantment
import io.github.rednesto.bou.tests.framework.mock.MockEnchantmentType
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.spongepowered.api.item.enchantment.Enchantment

class EnchantmentsRequirementTests {

    @Test
    fun `simple 1`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=1
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(mapOf("minecraft:efficiency" to FixedIntRange(1)), emptyMap())
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertFalse(expectedFilter())
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 2)))
    }

    @Test
    fun `simple 2`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=any
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(emptyMap(), emptyMap(), mapOf("minecraft:efficiency" to true))
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertFalse(expectedFilter())
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 2)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
    }

    @Test
    fun `simple 3`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=disallow
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(emptyMap(), emptyMap(), mapOf("minecraft:efficiency" to false))
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertTrue(expectedFilter())
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
    }

    @Test
    fun `simple 4`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=1-3
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(mapOf("minecraft:efficiency" to BoundedIntRange(1, 3)), emptyMap())
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertFalse(expectedFilter())
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 3)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
    }

    @Test
    fun `simple 5`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=1;2;4
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(mapOf("minecraft:efficiency" to SelectiveIntRange(1, 2, 4)), emptyMap())
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertFalse(expectedFilter())
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 4)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 3)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
    }

    @Test
    fun `simple 6`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=2
  "minecraft:silk_touch"=1
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(mapOf(
                "minecraft:efficiency" to FixedIntRange(2),
                "minecraft:silk_touch" to FixedIntRange(1)
        ), emptyMap())
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertFalse(expectedFilter())
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 2), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
    }

    @Test
    fun `disallow 1`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency".disallow=1
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(emptyMap(), mapOf(
                "minecraft:efficiency" to FixedIntRange(1)
        ))
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertTrue(expectedFilter())
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 2)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 2), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
    }

    @Test
    fun `disallow 2`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency".disallow=1-3
  "minecraft:silk_touch".disallow=1
  "minecraft:unbreaking"=disallow
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(emptyMap(), mapOf(
                "minecraft:efficiency" to BoundedIntRange(1, 3),
                "minecraft:silk_touch" to FixedIntRange(1)
        ), mapOf("minecraft:unbreaking" to false))
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertTrue(expectedFilter())
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 4)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 4), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.UNBREAKING, 1)))
        assertFalse(expectedFilter(
                MockEnchantment(MockEnchantmentType.EFFICIENCY, 1),
                MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1),
                MockEnchantment(MockEnchantmentType.FORTUNE, 1)
        ))
        assertFalse(expectedFilter(
                MockEnchantment(MockEnchantmentType.EFFICIENCY, 4),
                MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1),
                MockEnchantment(MockEnchantmentType.FORTUNE, 1)
        ))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1), MockEnchantment(MockEnchantmentType.FORTUNE, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 4), MockEnchantment(MockEnchantmentType.FORTUNE, 1)))
    }

    @Test
    fun `mixed 1`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=any
  "minecraft:silk_touch"=1
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(
                mapOf("minecraft:silk_touch" to FixedIntRange(1)),
                emptyMap(),
                mapOf("minecraft:efficiency" to true)
        )
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertFalse(expectedFilter())
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 3), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
    }

    @Test
    fun `mixed 2`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=any
  "minecraft:silk_touch"=disallow
  "minecraft:unbreaking"=1-3
  "minecraft:fortune"=1;3
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(
                mapOf("minecraft:unbreaking" to BoundedIntRange(1, 3), "minecraft:fortune" to SelectiveIntRange(1, 3)),
                emptyMap(),
                mapOf("minecraft:efficiency" to true, "minecraft:silk_touch" to false)
        )
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertFalse(expectedFilter())
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.FORTUNE, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 3), MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertTrue(expectedFilter(
                MockEnchantment(MockEnchantmentType.EFFICIENCY, 2),
                MockEnchantment(MockEnchantmentType.UNBREAKING, 1),
                MockEnchantment(MockEnchantmentType.FORTUNE, 1)
        ))
        assertFalse(expectedFilter(
                MockEnchantment(MockEnchantmentType.EFFICIENCY, 2),
                MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1),
                MockEnchantment(MockEnchantmentType.UNBREAKING, 1),
                MockEnchantment(MockEnchantmentType.FORTUNE, 1)
        ))
    }

    @Test
    fun `mixed 3`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=any
  "minecraft:unbreaking"=any
  "minecraft:silk_touch"=disallow
  "minecraft:fortune"=disallow
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(emptyMap(), emptyMap(), mapOf(
                "minecraft:efficiency" to true, "minecraft:unbreaking" to true,
                "minecraft:silk_touch" to false, "minecraft:fortune" to false
        ))
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertFalse(expectedFilter())
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.FORTUNE, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 3), MockEnchantment(MockEnchantmentType.UNBREAKING, 1)))
        assertFalse(expectedFilter(
                MockEnchantment(MockEnchantmentType.EFFICIENCY, 1),
                MockEnchantment(MockEnchantmentType.UNBREAKING, 1),
                MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)
        ))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.FORTUNE, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1), MockEnchantment(MockEnchantmentType.FORTUNE, 1)))
    }

    @Test
    fun `mixed 4`() {
        val requirement = loadRequirement("""
enchantments {
  "minecraft:efficiency"=1;5
  "minecraft:unbreaking"=any
  "minecraft:silk_touch"=disallow
  "minecraft:fortune".disallow=1-3
}
        """)

        val expectedFilter = SimpleEnchantmentsFilter(
                mapOf("minecraft:efficiency" to SelectiveIntRange(1, 5)),
                mapOf("minecraft:fortune" to BoundedIntRange(1, 3)),
                mapOf("minecraft:unbreaking" to true, "minecraft:silk_touch" to false))
        assertEquals(EnchantmentsRequirement(expectedFilter), requirement)

        assertFalse(expectedFilter())
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1)))
        assertTrue(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 1), MockEnchantment(MockEnchantmentType.UNBREAKING, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.EFFICIENCY, 2), MockEnchantment(MockEnchantmentType.UNBREAKING, 1)))
        assertFalse(expectedFilter(
                MockEnchantment(MockEnchantmentType.EFFICIENCY, 1),
                MockEnchantment(MockEnchantmentType.UNBREAKING, 1),
                MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)
        ))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.SILK_TOUCH, 1)))
        assertFalse(expectedFilter(MockEnchantment(MockEnchantmentType.FORTUNE, 1)))
    }

    private val configHelper: ConfigHelper = ConfigHelper.create {
        it.registerType(BouTypeTokens.ENCHANTMENTS_FILTER, EnchantmentsFilterSerializer())
                .registerType(BouTypeTokens.INT_RANGE, IntRangeSerializer())
    }

    private fun loadRequirement(@Language("HOCON") configuration: String): Requirement<*> {
        val config = configHelper.loadNode(configuration).getNode("enchantments")
        val provider = EnchantmentsRequirement.Provider()
        return provider.provide(config)
    }
}

operator fun EnchantmentsFilter.invoke(vararg enchantments: Enchantment): Boolean {
    return this.test(enchantments.toList())
}
