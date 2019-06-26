package io.github.rednesto.bou.tests

import io.github.rednesto.bou.common.Config
import io.github.rednesto.bou.common.CustomLoot
import io.github.rednesto.bou.common.ItemLoot
import io.github.rednesto.bou.common.MoneyLoot
import io.github.rednesto.bou.common.quantity.BoundedIntQuantity
import io.github.rednesto.bou.common.quantity.FixedIntQuantity
import io.github.rednesto.bou.sponge.BoxOUtils
import io.github.rednesto.bou.sponge.SpongeConfig
import io.github.rednesto.bou.sponge.config.serializers.BouTypeTokens
import io.github.rednesto.bou.sponge.requirements.DataByKeyRequirement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.slf4j.LoggerFactory
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockState
import java.nio.file.Paths

class BlocksDropsConfigurationTests {

    @Test
    fun `requirements 1`() {
        val (_, config) = prepare("requirements1")

        val requirements = listOf(DataByKeyRequirement("block_data", BlockSnapshot::class.java, mapOf("sponge_impl:skull_type" to listOf("minecraft:ender_dragon"))))
        val customLoot = CustomLoot(emptyList(), 0, false, false, requirements, null, null)
        val expected = mutableMapOf("minecraft:skull" to customLoot)

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple 1`() {
        println(DataByKeyRequirement("block_data", BlockState::class.java, mutableMapOf("test" to mutableListOf("value"))))
        val (_, config) = prepare("simple1")

        val itemLoots = listOf(ItemLoot("minecraft:cobblestone", null, null, 25.0, null))
        val customLoot = CustomLoot(itemLoots, 0, false, false, emptyList(), null, null)
        val expected = mapOf("minecraft:iron_ore" to customLoot)

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    @Test
    fun `simple 2`() {
        val (_, config) = prepare("simple2")

        val moneyLoot = MoneyLoot(BoundedIntQuantity(10, 30), "economylite:coin", 25.0, "&aYou earned {money_amount}")
        val itemLoots = listOf(ItemLoot("minecraft:coal", null, null, 25.0, FixedIntQuantity(1)))
        val customLoot = CustomLoot(itemLoots, 0, true, false, emptyList(), moneyLoot, null)
        val expected = mapOf("minecraft:leaves" to customLoot)

        assertTrue(config.enabled)
        assertEquals(expected, config.drops)
    }

    private fun prepare(testName: String): Pair<BoxOUtils, Config.BlocksDrops> {
        val folderUri = javaClass.getResource("/configurationTests/blocksdrops") ?: fail { "config folder does not exist" }
        val configDir = Paths.get(folderUri.toURI())
        val plugin = BoxOUtils(LoggerFactory.getLogger(BoxOUtils::class.java), configDir)
        val node = SpongeConfig.loader(configDir.resolve("$testName.conf")).load()
        return Pair(plugin, node.getValue(BouTypeTokens.CONFIG_BLOCKS_DROPS)!!)
    }
}
