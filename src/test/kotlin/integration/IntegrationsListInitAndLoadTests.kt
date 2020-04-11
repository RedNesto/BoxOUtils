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
package io.github.rednesto.bou.tests.integration

import io.github.rednesto.bou.BoxOUtils
import io.github.rednesto.bou.api.integration.Integration
import io.github.rednesto.bou.api.integration.IntegrationsList
import io.github.rednesto.bou.tests.framework.BouTestCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

class IntegrationsListInitAndLoadTests : BouTestCase(false) {

    @Test
    fun `load integration on register`() {
        val integrationsList = CustomIntegrationsList()
        val integration = InitTrackingIntegration("mod:dummy")
        integrationsList.initIntegrations(plugin)

        assertTrue(integrationsList.register(integration))

        assertEquals(1, integration.initCount)
        assertEquals(1, integration.loadCount)

        integrationsList.reloadIntegrations(plugin)
        assertEquals(1, integration.initCount)
        assertEquals(2, integration.loadCount)

        integrationsList.reloadIntegrations(plugin)
        assertEquals(1, integration.initCount)
        assertEquals(3, integration.loadCount)
    }

    @Test
    fun `load integration later`() {
        val integrationsList = CustomIntegrationsList()
        val integration = InitTrackingIntegration("mod:dummy")

        assertTrue(integrationsList.register(integration))

        assertEquals(0, integration.initCount)
        assertEquals(0, integration.loadCount)

        integrationsList.initIntegrations(plugin)
        assertEquals(1, integration.initCount)
        assertEquals(1, integration.loadCount)

        integrationsList.reloadIntegrations(plugin)
        assertEquals(1, integration.initCount)
        assertEquals(2, integration.loadCount)
    }

    @Test
    fun `load integration mixed`() {
        val integrationsList = CustomIntegrationsList()
        val integrationA = InitTrackingIntegration("mod:dummyA")
        val integrationB = InitTrackingIntegration("mod:dummyB")

        assertTrue(integrationsList.register(integrationA))

        assertEquals(0, integrationA.initCount)
        assertEquals(0, integrationA.loadCount)

        integrationsList.initIntegrations(plugin)
        assertEquals(1, integrationA.initCount)
        assertEquals(1, integrationA.loadCount)

        integrationsList.reloadIntegrations(plugin)
        assertEquals(1, integrationA.initCount)
        assertEquals(2, integrationA.loadCount)

        assertTrue(integrationsList.register(integrationB))

        assertEquals(1, integrationA.initCount)
        assertEquals(2, integrationA.loadCount)
        assertEquals(1, integrationB.initCount)
        assertEquals(1, integrationB.loadCount)

        integrationsList.reloadIntegrations(plugin)
        assertEquals(1, integrationA.initCount)
        assertEquals(3, integrationA.loadCount)
        assertEquals(1, integrationB.initCount)
        assertEquals(2, integrationB.loadCount)
    }

    @Test
    fun `exception thrown in integration init and load`() {
        val integrationsList = IntegrationsList<FailingInitAndLoadIntegration>("DummyIntegration")

        val integration = FailingInitAndLoadIntegration("mod_a:dummy")
        assertTrue(integrationsList.register(integration))

        assertDoesNotThrow { integrationsList.initIntegrations(plugin) }
    }

    override fun createConfigDir(): Path = Paths.get("")

    private class CustomIntegrationsList : IntegrationsList<InitTrackingIntegration>("DummyIntegration") {
        override fun shouldInitIntegrationOnRegistration(): Boolean = true
    }

    private class InitTrackingIntegration(val dummyId: String) : Integration {

        var initCount: Int = 0
            private set
        var loadCount: Int = 0
            private set

        override fun init(plugin: BoxOUtils) {
            initCount++
        }

        override fun load(plugin: BoxOUtils) {
            loadCount++
        }

        override fun getId(): String = dummyId
    }

    private class FailingInitAndLoadIntegration(val dummyId: String) : Integration {

        override fun init(plugin: BoxOUtils): Unit = throw Exception()

        override fun load(plugin: BoxOUtils): Unit = throw Exception()

        override fun getId(): String = dummyId
    }
}
