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

import io.github.rednesto.bou.common.Config
import io.github.rednesto.bou.common.CustomLoot
import io.github.rednesto.bou.common.ItemLoot
import io.github.rednesto.bou.common.MoneyLoot
import io.github.rednesto.bou.common.lootReuse.MultiplyLootReuse
import io.github.rednesto.bou.common.lootReuse.SimpleLootReuse
import io.github.rednesto.bou.common.quantity.BoundedIntQuantity
import io.github.rednesto.bou.sponge.BoxOUtils
import io.github.rednesto.bou.sponge.SpongeConfig
import io.github.rednesto.bou.sponge.config.serializers.BouTypeTokens
import io.github.rednesto.bou.sponge.integration.requirements.GriefPreventionRegionRequirement
import io.github.rednesto.bou.sponge.requirements.DataByKeyRequirement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.slf4j.LoggerFactory
import org.spongepowered.api.entity.EntitySnapshot
import java.nio.file.Paths

class MobsDropsConfigurationTests {

    @Test
    fun `complex 1`() {
        val (_, config) = prepare("complex1")

        val sheep = run {
            val requirements = listOf(
                    GriefPreventionRegionRequirement(listOf("test region"), false),
                    DataByKeyRequirement("entity_data", EntitySnapshot::class.java, mapOf("sponge_impl:dye_color" to listOf("minecraft:red"))))
            val reuseItems = mapOf(
                    "minecraft:wool" to MultiplyLootReuse(3f),
                    "minecraft:mutton" to SimpleLootReuse(BoundedIntQuantity(2, 5)))
            val reuse = CustomLoot.Reuse(2f, reuseItems, emptyList())
            val money = MoneyLoot(BoundedIntQuantity(5, 25), null, 50.0, "&aYou earned {money_amount}")
            val drops = listOf(
                    ItemLoot("waw_sword", "byte-items", null, 0.0, null),
                    ItemLoot("test", "file-inv", null, 0.0, BoundedIntQuantity(0, 2)))
            CustomLoot(drops, null, true, false, requirements, money, reuse)
        }

        val bat = run {
            val drops = listOf(ItemLoot("minecraft:ghast_tear", null, null, 33.33, null))
            CustomLoot(drops, null, false, false, emptyList(), null, null)
        }

        val expected = mapOf(
                "minecraft:sheep" to sheep,
                "minecraft:bat" to bat
        )

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    private fun prepare(testName: String): Pair<BoxOUtils, Config.MobsDrops> {
        val folderUri = javaClass.getResource("/configurationTests/mobsdrops") ?: fail { "config folder does not exist" }
        val configDir = Paths.get(folderUri.toURI())
        val plugin = BoxOUtils(LoggerFactory.getLogger(BoxOUtils::class.java), configDir)
        val node = SpongeConfig.loader(configDir.resolve("$testName.conf")).load()
        return Pair(plugin, node.getValue(BouTypeTokens.CONFIG_MOBS_DROPS)!!)
    }
}
