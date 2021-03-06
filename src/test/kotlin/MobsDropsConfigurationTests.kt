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
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.integration.customdrops.MoneyLootComponent
import io.github.rednesto.bou.integration.griefprevention.GriefPreventionRegionRequirement
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
                    DataByKeyRequirement("box-o-utils:entity_data", EntitySnapshot::class.java, mapOf("sponge_impl:dye_color" to listOf("minecraft:red")))))
            val reuseItems = mapOf(
                    "minecraft:mutton" to SimpleLootReuse(BoundedIntQuantity(2, 5)),
                    "minecraft:wool" to MultiplyLootReuse(3f))
            val reuse = customReuse(multiplier = 2f, items = reuseItems)
            val money = MoneyLootComponent(MoneyLoot(BoundedIntQuantity(5, 25), null, 50.0, "&aYou earned {money_amount}"))
            val drops = listOf(
                    byteItemsDrop("waw_sword"),
                    fileInvDrop("test", quantity = BoundedIntQuantity(0, 2)))
            customLoot(drops, overwrite = true, requirements = requirements, reuse = reuse, components = listOf(money))
        }

        val bat = run {
            val drops = listOf(vanillaDrop("minecraft:ghast_tear", chance = 33.33))
            customLoot(drops)
        }

        val expected = mapOf(
                "minecraft:bat" to listOf(bat),
                "minecraft:sheep" to listOf(sheep)
        )

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }
}
