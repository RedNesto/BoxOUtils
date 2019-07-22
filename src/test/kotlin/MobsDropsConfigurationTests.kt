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
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.integration.griefprevention.GriefPreventionRegionRequirement
import io.github.rednesto.bou.lootReuse.MultiplyLootReuse
import io.github.rednesto.bou.lootReuse.SimpleLootReuse
import io.github.rednesto.bou.models.CustomLoot
import io.github.rednesto.bou.models.ItemLoot
import io.github.rednesto.bou.models.MoneyLoot
import io.github.rednesto.bou.quantity.BoundedIntQuantity
import io.github.rednesto.bou.requirements.DataByKeyRequirement
import io.github.rednesto.bou.tests.framework.PluginConfigurationTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.spongepowered.api.entity.EntitySnapshot

class MobsDropsConfigurationTests : PluginConfigurationTestCase<Config.MobsDrops>("mobsdrops", BouTypeTokens.CONFIG_MOBS_DROPS) {

    @Test
    fun `complex 1`() {
        val config = loadConfig("complex1")

        val sheep = run {
            val requirements = listOf(listOf(
                    GriefPreventionRegionRequirement(listOf("test region"), false),
                    DataByKeyRequirement("entity_data", EntitySnapshot::class.java, mapOf("sponge_impl:dye_color" to listOf("minecraft:red")))))
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
}
