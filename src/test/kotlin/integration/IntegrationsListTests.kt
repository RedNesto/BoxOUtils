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

    @Test
    fun `default namespace`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration", "mod_a")

        val integration = DummyIntegration("mod_a:dummy")
        assertTrue(integrationsList.register(integration))

        assertSame(integration, integrationsList.getById("dummy"))
    }

    @Test
    fun `default namespace fail`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration")

        assertTrue(integrationsList.register(DummyIntegration("mod_a:dummy")))

        assertNull(integrationsList.getById("dummy"))
    }

    @Test
    fun `default namespace with short ID ambiguity`() {
        val integrationsList = IntegrationsList<DummyIntegration>("DummyIntegration", "mod_a")

        val integration = DummyIntegration("mod_a:dummy")
        assertTrue(integrationsList.register(integration))
        assertTrue(integrationsList.register(DummyIntegration("mod_b:dummy"), true))

        assertSame(integration, integrationsList.getById("dummy"))
    }

    private class DummyIntegration(val dummyId: String) : Integration {
        override fun getId(): String = dummyId
    }
}
