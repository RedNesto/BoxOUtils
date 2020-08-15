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
import io.github.rednesto.bou.api.customdrops.MoneyLoot
import io.github.rednesto.bou.api.lootReuse.MultiplyLootReuse
import io.github.rednesto.bou.api.lootReuse.SimpleLootReuse
import io.github.rednesto.bou.api.quantity.BoundedIntQuantity
import io.github.rednesto.bou.api.quantity.FixedIntQuantity
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.integration.customdrops.MoneyLootComponent
import io.github.rednesto.bou.integration.customdrops.recipients.PlayerInventoryLootRecipient
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
            val drops = listOf(vanillaDrop("minecraft:coal", chance = 25.0, quantity = FixedIntQuantity(1)))
            val money = MoneyLootComponent(MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}"))
            customLoot(drops, overwrite = true, components = listOf(money))
        }

        val leaves2 = run {
            val drops = listOf(
                    vanillaDrop("minecraft:coal", chance = 25.0, quantity = FixedIntQuantity(1)),
                    vanillaDrop("minecraft:dirt", chance = 25.0, quantity = FixedIntQuantity(1)))
            val money  = MoneyLootComponent(MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}"))
            customLoot(drops, overwrite = true, components = listOf(money))
        }

        val ironOre = run {
            val reuse = customReuse(multiplier = 2f, items = mapOf("minecraft:iron_ore" to SimpleLootReuse(BoundedIntQuantity(1, 3))))
            val money = MoneyLootComponent(MoneyLoot(BoundedIntQuantity(1, 15), null, 25.0, "&aYou earned {money_amount}"))
            val drops = listOf(vanillaDrop("minecraft:cobblestone", chance = 25.0))
            customLoot(drops, reuse = reuse, components = listOf(money))
        }

        val skull = run {
            val requirements = listOf(listOf(GriefPreventionRegionRequirement(listOf("test region"), true)))
            val reuse = customReuse(multiplier = 2f, requirements = listOf(listOf(DataByKeyRequirement("box-o-utils:block_data", BlockSnapshot::class.java, mapOf("sponge_impl:skull_type" to listOf("minecraft:ender_dragon"))))))
            customLoot(requirements = requirements, reuse = reuse)
        }

        val wheat = run {
            val reuseItems = mapOf(
                    "minecraft:seed" to MultiplyLootReuse(3f),
                    "minecraft:wheat" to SimpleLootReuse(BoundedIntQuantity(1, 5))
            )
            val reuse = customReuse(items = reuseItems)
            customLoot(reuse = reuse)
        }

        val stone = run {
            val drops = listOf(vanillaDrop("minecraft:stone", unsafeDamage = 16))
            customLoot(drops)
        }

        val expected = mapOf(
                "minecraft:leaves" to listOf(leaves),
                "minecraft:leaves2" to listOf(leaves2),
                "minecraft:iron_ore" to listOf(ironOre),
                "minecraft:skull" to listOf(skull),
                "minecraft:wheat" to listOf(wheat),
                "minecraft:stone" to listOf(stone)
        )

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `drops by provider 1`() {
        val config = loadConfig("dropsByProvider1")

        val drops = listOf(
                byteItemsDrop("the_item"),
                vanillaDrop("minecraft:cobblestone")
        )
        val customLoot = customLoot(drops)
        val expected = mapOf("minecraft:iron_ore" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `drops by provider 2`() {
        val config = loadConfig("dropsByProvider2")

        val drops = listOf(
                vanillaDrop("minecraft:cobblestone"),
                byteItemsDrop("the_item", quantity = FixedIntQuantity(2))
        )
        val customLoot = customLoot(drops)
        val expected = mapOf("minecraft:iron_ore" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `drops by provider 3`() {
        val config = loadConfig("dropsByProvider3")

        val drops = listOf(
                byteItemsDrop("the_item", chance = 10.0),
                byteItemsDrop("the_item"),
                vanillaDrop("minecraft:cobblestone"),
                vanillaDrop("minecraft:stone", chance = 25.0)
        )
        val customLoot = customLoot(drops)
        val expected = mapOf("minecraft:iron_ore" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `mixed drops 1`() {
        val config = loadConfig("mixedDrops1")

        val drops = listOf(
                vanillaDrop("minecraft:stone"),
                vanillaDrop("minecraft:cobblestone", chance = 25.0),
                byteItemsDrop("the_item", quantity = FixedIntQuantity(2))
        )
        val customLoot = customLoot(drops)
        val expected = mapOf("minecraft:iron_ore" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `requirements 1`() {
        val config = loadConfig("requirements1")

        val requirements = listOf(listOf(DataByKeyRequirement("box-o-utils:block_data", BlockSnapshot::class.java, mapOf("sponge_impl:skull_type" to listOf("minecraft:ender_dragon")))))
        val customLoot = customLoot(requirements = requirements)
        val expected = mutableMapOf("minecraft:skull" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple 1`() {
        val config = loadConfig("simple1")

        val itemLoots = listOf(vanillaDrop("minecraft:cobblestone", chance = 25.0))
        val customLoot = customLoot(itemLoots, recipient = PlayerInventoryLootRecipient.INSTANCE)
        val expected = mapOf("minecraft:iron_ore" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple 2`() {
        val config = loadConfig("simple2")

        val moneyLoot = MoneyLootComponent(MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}"))
        val itemLoots = listOf(vanillaDrop("minecraft:coal", chance = 25.0, quantity = FixedIntQuantity(1)))
        val customLoot = customLoot(itemLoots, overwrite = true, components = listOf(moneyLoot))
        val expected = mapOf("minecraft:leaves" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple drop 1`() {
        val config = loadConfig("simpleDrop1")

        val drops = listOf(vanillaDrop("minecraft:cobblestone"))
        val customLoot = customLoot(drops)
        val expected = mapOf("minecraft:iron_ore" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple drop 2`() {
        val config = loadConfig("simpleDrop2")

        val drops = listOf(vanillaDrop("minecraft:cobblestone", quantity = BoundedIntQuantity(0, 2)))
        val customLoot = customLoot(drops)
        val expected = mapOf("minecraft:iron_ore" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simplest drop`() {
        val config = loadConfig("simplestDrop")

        val drops = listOf(vanillaDrop("minecraft:cobblestone"))
        val customLoot = customLoot(drops)
        val expected = mapOf("minecraft:iron_ore" to listOf(customLoot))

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }
}
