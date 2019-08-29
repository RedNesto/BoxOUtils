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

import io.github.rednesto.bou.api.customdrops.CustomLootCommand
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.config.serializers.CustomLootCommandSerializer
import io.github.rednesto.bou.config.serializers.RequirementSerializer
import io.github.rednesto.bou.config.serializers.RequirementsMapSerializer
import io.github.rednesto.bou.integration.griefprevention.GriefPreventionRegionRequirement
import io.github.rednesto.bou.tests.framework.ConfigurationTestCase
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CustomLootCommandTests : ConfigurationTestCase<CustomLootCommand>("commands", BouTypeTokens.CUSTOM_LOOT_COMMAND) {

    @Test
    fun `single simple command`() {
        val loaded = loadConfig("commands=\"test command\"")
        val expected = CustomLootCommand("test command", CustomLootCommand.SenderMode.SERVER, listOf())
        assertEquals(expected, loaded)
    }

    @Test
    fun `single complex command 1`() {
        val config = """
commands {
  command="test command"
}
"""
        val expected = CustomLootCommand("test command", CustomLootCommand.SenderMode.SERVER, listOf())
        assertEquals(expected, loadConfig(config))
    }

    @Test
    fun `single complex command 2`() {
        val config = """
commands {
  command="test command"
  as=PLAYER
}
"""
        val expected = CustomLootCommand("test command", CustomLootCommand.SenderMode.PLAYER, listOf())
        assertEquals(expected, loadConfig(config))
    }

    @Test
    fun `several simple commands`() {
        val config = """
commands=[
  "command 1"
  "command 2"
  "command 3"
]
"""
        val expected = listOf(
                CustomLootCommand("command 1", CustomLootCommand.SenderMode.SERVER, listOf()),
                CustomLootCommand("command 2", CustomLootCommand.SenderMode.SERVER, listOf()),
                CustomLootCommand("command 3", CustomLootCommand.SenderMode.SERVER, listOf())
        )
        assertEquals(expected, loadConfigList(config))
    }

    @Test
    fun `several mixed commands`() {
        val config = """
commands=[
  {
    command="command 1"
    as=PLAYER
  }
  {
    command="command 2"
    requirements {
      griefprevention {
        list-type=WHITELIST
        regions=[
          "test region"
        ]
      }
    }
  }
  "command 3"
]
"""
        val expected = listOf(
                CustomLootCommand("command 1", CustomLootCommand.SenderMode.PLAYER, listOf()),
                CustomLootCommand("command 2", CustomLootCommand.SenderMode.SERVER, listOf(listOf(GriefPreventionRegionRequirement(listOf("test region"), true)))),
                CustomLootCommand("command 3", CustomLootCommand.SenderMode.SERVER, listOf())
        )
        assertEquals(expected, loadConfigList(config))
    }

    override fun populateSerializers(serializers: TypeSerializerCollection) {
        serializers.registerType(BouTypeTokens.CUSTOM_LOOT_COMMAND, CustomLootCommandSerializer())
                .registerType(BouTypeTokens.REQUIREMENT, RequirementSerializer())
                .registerType(BouTypeTokens.REQUIREMENTS_MAP, RequirementsMapSerializer())
    }
}