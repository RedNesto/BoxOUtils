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

import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext
import io.github.rednesto.bou.requirements.PermissionsRequirement
import io.github.rednesto.bou.tests.framework.mock.MockPlayer
import io.github.rednesto.bou.tests.lootProcessingContext
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext

class PermissionsRequirementTests {

    @Test
    fun `single permission`() {
        assertTrue(doFulfills(listOf("test_perm"), listOf("test_perm")))
    }

    @Test
    fun `several permissions`() {
        assertTrue(doFulfills(listOf("test_perm_1", "test_perm_2"), listOf("test_perm_1")))
    }

    @Test
    fun `missing permission`() {
        assertFalse(doFulfills(listOf("test_perm"), listOf("test_another_perm")))
    }

    private fun doFulfills(requirementPerms: List<String>, playerPerms: List<String>): Boolean {
        val requirement = PermissionsRequirement(requirementPerms)
        val mockPlayer = MockPlayer(playerPerms)
        val cause = Cause.of(EventContext.empty(), mockPlayer)
        val context = lootProcessingContext(emptyList(), Any(), cause);
        return requirement.fulfills(context)
    }
}
