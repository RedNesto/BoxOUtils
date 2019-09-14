package io.github.rednesto.bou.tests.integration

import io.github.rednesto.bou.api.integration.Integration
import io.github.rednesto.bou.api.integration.IntegrationsList
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class IntegrationsListTests {

    @Test
    fun `simple registration`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration")

        assertTrue(integrationsList.register(DummyIntegration("modid:dummy")))
    }

    @Test
    fun `missing namespace`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration")

        assertFalse(integrationsList.register(DummyIntegration("dummy")))

        assertNull(integrationsList.getById("dummy"))
    }

    @Test
    fun `invalid id 1`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration")

        assertFalse(integrationsList.register(DummyIntegration("the mod:some:integration")))

        assertNull(integrationsList.getById("the mod:some:integration"))
    }

    @Test
    fun `invalid id 2`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration")

        assertFalse(integrationsList.register(DummyIntegration("modid:")))

        assertNull(integrationsList.getById("modid:"))
    }

    @Test
    fun `duplicate registration`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration")
        val dummyIntegration = DummyIntegration("modid:dummy")

        assertTrue(integrationsList.register(dummyIntegration))
        assertFalse(integrationsList.register(dummyIntegration))

        assertSame(dummyIntegration, integrationsList.getById("modid:dummy"))
    }

    @Test
    fun `short ID`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration")
        val modAIntegration = DummyIntegration("mod_a:dummy")

        assertTrue(integrationsList.register(modAIntegration, true))

        assertSame(modAIntegration, integrationsList.getByShortId("dummy"))
    }

    @Test
    fun `short ID conflict`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration")

        assertTrue(integrationsList.register(DummyIntegration("mod_a:dummy"), true))
        assertTrue(integrationsList.register(DummyIntegration("mod_b:dummy"), true))
        assertTrue(integrationsList.register(DummyIntegration("mod_c:dummy"), true))

        assertNull(integrationsList.getByShortId("dummy"))
    }

    private class DummyIntegration(val dummyId: String) : Integration {
        override fun getId(): String = dummyId
    }
}
