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
import com.google.common.reflect.TypeToken
import io.github.rednesto.bou.api.requirement.AbstractRequirement
import io.github.rednesto.bou.api.requirement.Requirement
import io.github.rednesto.bou.api.requirement.RequirementProvider
import io.github.rednesto.bou.config.serializers.BouTypeTokens
import io.github.rednesto.bou.config.serializers.RequirementSerializer
import io.github.rednesto.bou.config.serializers.RequirementsMapSerializer
import io.github.rednesto.bou.tests.framework.BouFixture
import io.github.rednesto.bou.tests.framework.ConfigurationTestCase
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.util.TypeTokens
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

private val TOKEN = object : TypeToken<MutableList<MutableList<Requirement<*>>>>() {}

class RequirementsConfigurationTests : ConfigurationTestCase<MutableList<MutableList<Requirement<*>>>>("requirements", TOKEN) {

    val pluginFixture = BouFixture({ Paths.get("config") })

    @Test
    fun `single requirement`() {
        val config = """
requirements {
  req1="some string"
}
"""
        val expected = listOf(listOf(TestRequirement("bou-test:req1", "some string")))
        assertEquals(expected, loadConfig(config))
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
                TestRequirement("bou-test:req1", "some string"),
                TestRequirement("bou-test:req2", listOf("yes", "no", "maybe"))))
        assertEquals(expected, loadConfig(config))
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
                listOf(TestRequirement("bou-test:req1", "some string")),
                listOf(TestRequirement("bou-test:req2", listOf("wow", "many choices")),
                        TestRequirement("bou-test:req3", RequirementDataStruct("text", 5))))
        val loaded = loadConfig(config)
        // We sort those requirements to have the same order than the expected one
        loaded[1].sortBy { it.id }
        assertEquals(expected, loaded)
    }

    override fun populateSerializers(serializers: TypeSerializerCollection) {
        serializers.registerType(BouTypeTokens.REQUIREMENT, RequirementSerializer())
                .registerType(BouTypeTokens.REQUIREMENTS_MAP, RequirementsMapSerializer())
    }

    override fun loadConfig(configuration: String): MutableList<MutableList<Requirement<*>>> {
        val rootNode = configHelper.loadNode(configuration)
        return RequirementSerializer.getRequirementGroups(rootNode.getNode("requirements"))
    }

    @BeforeEach
    private fun setUp() {
        pluginFixture.setUp()
        val integrationsManager = pluginFixture.plugin.integrationsManager.requirementsProviderIntegrations
        integrationsManager.register(TestRequirement.Provider("bou-test:req1") { it.string!! }, true)
        integrationsManager.register(TestRequirement.Provider("bou-test:req2") {
            ArrayList(it.getList(TypeTokens.STRING_TOKEN))
        }, true)
        integrationsManager.register(TestRequirement.Provider("bou-test:req3") {
            RequirementDataStruct(it.getNode("str").string!!, it.getNode("int").int)
        }, true)
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
                .toString()
    }

    class Provider(private val id: String, private val configLoader: (node: ConfigurationNode) -> Any) : RequirementProvider {

        override fun getId(): String = id

        override fun provide(node: ConfigurationNode): Requirement<*> {
            return TestRequirement(id, configLoader(node))
        }
    }
}

private data class RequirementDataStruct(val string: String, val int: Int)
