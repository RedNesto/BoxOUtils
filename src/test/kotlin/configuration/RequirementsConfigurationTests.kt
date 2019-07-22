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

import com.google.common.base.MoreObjects
import io.github.rednesto.bou.IntegrationsManager
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.config.serializers.RequirementSerializer
import io.github.rednesto.bou.config.serializers.RequirementsMapSerializer
import io.github.rednesto.bou.requirement.AbstractRequirement
import io.github.rednesto.bou.requirement.Requirement
import io.github.rednesto.bou.requirement.RequirementProvider
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.util.TypeTokens
import java.util.*
import kotlin.collections.ArrayList

class RequirementsConfigurationTests {

    @Test
    fun `single requirement`() {
        val config = """
requirements {
  req1="some string"
}
"""
        val expected = listOf(listOf(TestRequirement("req1", "some string")))
        assertEquals(expected, load(config))
    }

    @Test
    fun `several requirements`() {
        val config = """
requirements {
  req1="some string"
  req2=["yes", "no", "maybe"]
}
"""
        val expected = listOf(listOf(
                TestRequirement("req1", "some string"),
                TestRequirement("req2", listOf("yes", "no", "maybe"))))
        assertEquals(expected, load(config))
    }

    @Test
    fun `requirements groups`() {
        val config = """
requirements=[
  {
    req1="some string"
  }
  {
    req2=["wow", "many choices"]
    req3 {
      str="text"
      int=5
    }
  }
]
"""
        val expected = listOf(
                listOf(TestRequirement("req1", "some string")),
                listOf(TestRequirement("req2", listOf("wow", "many choices")),
                        TestRequirement("req3", RequirementDataStruct("text", 5))))
        val loaded = load(config)
        // We sort those requirements to have the same order than the expected one
        loaded?.get(1)?.sortBy { it.id }
        assertEquals(expected, loaded)
    }

    private val loaderOptions = ConfigurationOptions.defaults()
            .setSerializers(TypeSerializers.newCollection()
                    .registerType(BouTypeTokens.REQUIREMENT, RequirementSerializer())
                    .registerType(BouTypeTokens.REQUIREMENTS_MAP, RequirementsMapSerializer()))

    private fun load(configuration: String): MutableList<MutableList<Requirement<*>>>? {
        val loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(loaderOptions)
                .setSource { configuration.reader().buffered() }
                .build()

        val rootNode = loader.load()
        return RequirementSerializer.getRequirementGroups(rootNode.getNode("requirements"))
    }

    companion object {
        @JvmStatic
        @BeforeAll
        private fun setUp() {
            IntegrationsManager.getInstance().register(TestRequirement.Provider("req1") { node ->
                return@Provider node.string!!
            })
            IntegrationsManager.getInstance().register(TestRequirement.Provider("req2") { node ->
                ArrayList(node.getList(TypeTokens.STRING_TOKEN))
            })
            IntegrationsManager.getInstance().register(TestRequirement.Provider("req3") { node ->
                RequirementDataStruct(node.getNode("str").string!!, node.getNode("int").int)
            })
        }
    }
}

private class TestRequirement(id: String, val configurationValue: Any) : AbstractRequirement<Any>(id, Any::class.java) {

    // We don't care about fulfills since we only test requirements configuration loading here
    override fun fulfills(source: Any, cause: Cause): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestRequirement) return false
        if (!super.equals(other)) return false

        if (configurationValue != other.configurationValue) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(id, applicableType, configurationValue)
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("applicableType", applicableType)
                .add("configurationValue", configurationValue)
                .toString();
    }

    class Provider(private val id: String, private val configLoader: (node: ConfigurationNode) -> Any) : RequirementProvider {

        override fun getId(): String = id

        override fun provide(node: ConfigurationNode): Requirement<*> {
            return TestRequirement(id, configLoader(node))
        }
    }
}

private data class RequirementDataStruct(val string: String, val int: Int)
