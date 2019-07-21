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
package io.github.rednesto.bou.tests.requirements

import io.github.rednesto.bou.sponge.CustomDropsProcessor
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext

class RequirementsProcessorTests {

    @Test
    fun `empty requirements`() {
        assertTrue(testFulfill(listOf(listOf())))
    }

    @Test
    fun `empty requirements group`() {
        assertTrue(testFulfill(listOf()))
    }

    @Test
    fun `one requirement true`() {
        val requirements = listOf(listOf(ConstantRequirement(true)))
        assertTrue(testFulfill(requirements))
    }

    @Test
    fun `one requirement false`() {
        val requirements = listOf(listOf(ConstantRequirement(false)))
        assertFalse(testFulfill(requirements))
    }

    @Test
    fun `several requirements true`() {
        val requirements = listOf(listOf(ConstantRequirement(true), ConstantRequirement(true)))
        assertTrue(testFulfill(requirements))
    }

    @Test
    fun `several requirements false`() {
        val requirements = listOf(listOf(ConstantRequirement(false), ConstantRequirement(false)))
        assertFalse(testFulfill(requirements))
    }

    @Test
    fun `several requirements mixed`() {
        val requirements = listOf(listOf(ConstantRequirement(true), ConstantRequirement(false)))
        assertFalse(testFulfill(requirements))
    }

    @Test
    fun `single requirement groups true`() {
        val requirements = listOf(
                listOf(ConstantRequirement(true)),
                listOf(ConstantRequirement(true))
        )
        assertTrue(testFulfill(requirements))
    }

    @Test
    fun `single requirement groups false`() {
        val requirements = listOf(
                listOf(ConstantRequirement(false)),
                listOf(ConstantRequirement(false))
        )
        assertFalse(testFulfill(requirements))
    }

    @Test
    fun `single requirement groups mixed`() {
        val requirements = listOf(
                listOf(ConstantRequirement(true)),
                listOf(ConstantRequirement(false))
        )
        assertTrue(testFulfill(requirements))
    }

    @Test
    fun `several requirements groups true`() {
        val requirements = listOf(
                listOf(ConstantRequirement(true), ConstantRequirement(true)),
                listOf(ConstantRequirement(true))
        )
        assertTrue(testFulfill(requirements))
    }

    @Test
    fun `several requirements groups false`() {
        val requirements = listOf(
                listOf(ConstantRequirement(false), ConstantRequirement(false)),
                listOf(ConstantRequirement(false))
        )
        assertFalse(testFulfill(requirements))
    }

    @Test
    fun `several requirements groups mixed 1`() {
        val requirements = listOf(
                listOf(ConstantRequirement(true), ConstantRequirement(false)),
                listOf(ConstantRequirement(false))
        )
        assertFalse(testFulfill(requirements))
    }

    @Test
    fun `several requirements groups mixed 2`() {
        val requirements = listOf(
                listOf(ConstantRequirement(false), ConstantRequirement(true)),
                listOf(ConstantRequirement(true))
        )
        assertTrue(testFulfill(requirements))
    }

    @Test
    fun `several requirements groups mixed 3`() {
        val requirements = listOf(
                listOf(ConstantRequirement(true), ConstantRequirement(true)),
                listOf(ConstantRequirement(false))
        )
        assertTrue(testFulfill(requirements))
    }

    private fun testFulfill(requirements: List<List<ConstantRequirement>>): Boolean {
        return CustomDropsProcessor.fulfillsRequirements(Any(), Cause.of(EventContext.empty(), Any()), requirements)
    }
}
