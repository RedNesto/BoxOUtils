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
import io.github.rednesto.bou.api.customdrops.CustomLoot
import io.github.rednesto.bou.api.customdrops.ItemLoot
import io.github.rednesto.bou.api.customdrops.MoneyLoot
import io.github.rednesto.bou.api.lootReuse.MultiplyLootReuse
import io.github.rednesto.bou.api.lootReuse.SimpleLootReuse
import io.github.rednesto.bou.api.quantity.BoundedIntQuantity
import io.github.rednesto.bou.api.quantity.FixedIntQuantity
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.integration.customdrops.MoneyLootComponent
import io.github.rednesto.bou.integration.griefprevention.GriefPreventionRegionRequirement
import io.github.rednesto.bou.requirements.DataByKeyRequirement
import io.github.rednesto.bou.tests.framework.PluginConfigurationTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.spongepowered.api.block.BlockSnapshot

class BlocksDropsConfigurationTests : PluginConfigurationTestCase<Config.BlocksDrops>("blocksdrops", BouTypeTokens.CONFIG_BLOCKS_DROPS) {

    @Test
    fun `complex 1`() {
        val config = loadConfig("complex1")

        val leaves = run {
            val drops = listOf(ItemLoot("minecraft:coal", null, null, 25.0, FixedIntQuantity(1)))
            val money  = MoneyLootComponent(MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}"))
            CustomLoot(drops, true, false, emptyList(), null, listOf(money))
        }

        val leaves2 = run {
            val drops = listOf(
                    ItemLoot("minecraft:coal", null, null, 25.0, FixedIntQuantity(1)),
                    ItemLoot("minecraft:dirt", null, null, 25.0, FixedIntQuantity(1)))
            val money  = MoneyLootComponent(MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}"))
            CustomLoot(drops, true, false, emptyList(), null, listOf(money))
        }

        val ironOre = run {
            val reuse = CustomLoot.Reuse(2f, mapOf("minecraft:iron_ore" to SimpleLootReuse(BoundedIntQuantity(1, 3))), emptyList())
            val money = MoneyLootComponent(MoneyLoot(BoundedIntQuantity(1, 15), null, 25.0, "&aYou earned {money_amount}"))
            val drops = listOf(ItemLoot("minecraft:cobblestone", null, null, 25.0, null))
            CustomLoot(drops, false, false, emptyList(), reuse, listOf(money))
        }

        val skull = run {
            val requirements = listOf(listOf(GriefPreventionRegionRequirement(listOf("test region"), true)))
            val reuse = CustomLoot.Reuse(2f, emptyMap(), listOf(listOf(DataByKeyRequirement("box-o-utils:block_data", BlockSnapshot::class.java, mapOf("sponge_impl:skull_type" to listOf("minecraft:ender_dragon"))))))
            CustomLoot(emptyList(), false, false, requirements, reuse, emptyList())
        }

        val wheat = run {
            val reuseItems = mapOf(
                    "minecraft:seed" to MultiplyLootReuse(3f),
                    "minecraft:wheat" to SimpleLootReuse(BoundedIntQuantity(1, 5))
            )
            val reuse = CustomLoot.Reuse(1f, reuseItems, emptyList())
            CustomLoot(emptyList(), false, false, emptyList(), reuse, emptyList())
        }

        val expected = mapOf(
                "minecraft:leaves" to listOf(leaves),
                "minecraft:leaves2" to listOf(leaves2),
                "minecraft:iron_ore" to listOf(ironOre),
                "minecraft:skull" to listOf(skull),
                "minecraft:wheat" to listOf(wheat)
        )

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `requirements 1`() {
        val config = loadConfig("requirements1")

        val requirements = listOf(listOf(DataByKeyRequirement("box-o-utils:block_data", BlockSnapshot::class.java, mapOf("sponge_impl:skull_type" to listOf("minecraft:ender_dragon")))))
        val customLoot = CustomLoot(emptyList(), false, false, requirements, null, emptyList())
        val expected = mutableMapOf("minecraft:skull" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple 1`() {
        val config = loadConfig("simple1")

        val itemLoots = listOf(ItemLoot("minecraft:cobblestone", null, null, 25.0, null))
        val customLoot = CustomLoot(itemLoots, false, false, emptyList(), null, emptyList())
        val expected = mapOf("minecraft:iron_ore" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple 2`() {
        val config = loadConfig("simple2")

        val moneyLoot = MoneyLootComponent(MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}"))
        val itemLoots = listOf(ItemLoot("minecraft:coal", null, null, 25.0, FixedIntQuantity(1)))
        val customLoot = CustomLoot(itemLoots, true, false, emptyList(), null, listOf(moneyLoot))
        val expected = mapOf("minecraft:leaves" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }
}
