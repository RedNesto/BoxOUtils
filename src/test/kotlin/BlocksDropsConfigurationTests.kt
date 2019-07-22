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

import io.github.rednesto.bou.*
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.integration.griefprevention.GriefPreventionRegionRequirement
import io.github.rednesto.bou.lootReuse.MultiplyLootReuse
import io.github.rednesto.bou.lootReuse.SimpleLootReuse
import io.github.rednesto.bou.models.CustomLoot
import io.github.rednesto.bou.models.ItemLoot
import io.github.rednesto.bou.models.MoneyLoot
import io.github.rednesto.bou.quantity.BoundedIntQuantity
import io.github.rednesto.bou.quantity.FixedIntQuantity
import io.github.rednesto.bou.requirements.DataByKeyRequirement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.slf4j.LoggerFactory
import org.spongepowered.api.block.BlockSnapshot
import java.nio.file.Paths

class BlocksDropsConfigurationTests {

    @Test
    fun `complex 1`() {
        val (_, config) = prepare("complex1")

        val leaves = run {
            val drops = listOf(ItemLoot("minecraft:coal", null, null, 25.0, FixedIntQuantity(1)))
            val money  = MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}")
            CustomLoot(drops, null, true, false, emptyList(), money, null)
        }

        val leaves2 = run {
            val drops = listOf(
                    ItemLoot("minecraft:coal", null, null, 25.0, FixedIntQuantity(1)),
                    ItemLoot("minecraft:dirt", null, null, 25.0, FixedIntQuantity(1)))
            val money  = MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}")
            CustomLoot(drops, null, true, false, emptyList(), money, null)
        }

        val ironOre = run {
            val reuse = CustomLoot.Reuse(2f, mapOf("minecraft:iron_ore" to SimpleLootReuse(BoundedIntQuantity(1, 3))), emptyList())
            val money = MoneyLoot(BoundedIntQuantity(1, 15), null, 25.0, "&aYou earned {money_amount}")
            val drops = listOf(ItemLoot("minecraft:cobblestone", null, null, 25.0, null))
            CustomLoot(drops, null, false, false, emptyList(), money, reuse)
        }

        val skull = run {
            val requirements = listOf(listOf(GriefPreventionRegionRequirement(listOf("test region"), true)))
            val reuse = CustomLoot.Reuse(2f, emptyMap(), listOf(listOf(DataByKeyRequirement("block_data", BlockSnapshot::class.java, mapOf("sponge_impl:skull_type" to listOf("minecraft:ender_dragon"))))))
            CustomLoot(emptyList(), null, false, false, requirements, null, reuse)
        }

        val wheat = run {
            val reuseItems = mapOf(
                    "minecraft:seed" to MultiplyLootReuse(3f),
                    "minecraft:wheat" to SimpleLootReuse(BoundedIntQuantity(1, 5))
            )
            val reuse = CustomLoot.Reuse(1f, reuseItems, emptyList())
            CustomLoot(emptyList(), null, false, false, emptyList(), null, reuse)
        }

        val expected = mapOf(
                "minecraft:leaves" to leaves,
                "minecraft:leaves2" to leaves2,
                "minecraft:iron_ore" to ironOre,
                "minecraft:skull" to skull,
                "minecraft:wheat" to wheat
        )

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `requirements 1`() {
        val (_, config) = prepare("requirements1")

        val requirements = listOf(listOf(DataByKeyRequirement("block_data", BlockSnapshot::class.java, mapOf("sponge_impl:skull_type" to listOf("minecraft:ender_dragon")))))
        val customLoot = CustomLoot(emptyList(), null, false, false, requirements, null, null)
        val expected = mutableMapOf("minecraft:skull" to customLoot)

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple 1`() {
        val (_, config) = prepare("simple1")

        val itemLoots = listOf(ItemLoot("minecraft:cobblestone", null, null, 25.0, null))
        val customLoot = CustomLoot(itemLoots, null, false, false, emptyList(), null, null)
        val expected = mapOf("minecraft:iron_ore" to customLoot)

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple 2`() {
        val (_, config) = prepare("simple2")

        val moneyLoot = MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}")
        val itemLoots = listOf(ItemLoot("minecraft:coal", null, null, 25.0, FixedIntQuantity(1)))
        val customLoot = CustomLoot(itemLoots, null, true, false, emptyList(), moneyLoot, null)
        val expected = mapOf("minecraft:leaves" to customLoot)

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    private fun prepare(testName: String): Pair<BoxOUtils, Config.BlocksDrops> {
        val folderUri = javaClass.getResource("/configurationTests/blocksdrops") ?: fail { "config folder does not exist" }
        val configDir = Paths.get(folderUri.toURI())
        val plugin = BoxOUtils(LoggerFactory.getLogger(BoxOUtils::class.java), configDir, IntegrationsManager())
        BoxOUtils.setInstance(plugin)
        val integrationsManager = plugin.integrationsManager
        integrationsManager.loadVanillaBuiltins()
        BouUtils.registerIntegrations(integrationsManager, true)
        val node = SpongeConfig.loader(configDir.resolve("$testName.conf")).load()
        return Pair(plugin, node.getValue(BouTypeTokens.CONFIG_BLOCKS_DROPS)!!)
    }
}
