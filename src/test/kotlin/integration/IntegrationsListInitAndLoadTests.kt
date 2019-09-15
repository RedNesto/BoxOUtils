package io.github.rednesto.bou.tests.integration

import io.github.rednesto.bou.BoxOUtils
import io.github.rednesto.bou.api.integration.Integration
import io.github.rednesto.bou.api.integration.IntegrationsList
import io.github.rednesto.bou.tests.framework.BouTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

    override fun createConfigDir(): Path = Paths.get("")

    private class CustomIntegrationsList : IntegrationsList<InitTrackingIntegration>("DummyIntegration") {
        override fun shouldLoadIntegrationOnRegistration(): Boolean = true
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
}
