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

import io.github.rednesto.bou.requirements.WorldsRequirement
import io.github.rednesto.bou.tests.framework.mock.MockServerLocation
import io.github.rednesto.bou.tests.framework.mock.MockServerPlayer
import io.github.rednesto.bou.tests.framework.mock.MockServerWorld
import io.github.rednesto.bou.tests.framework.mock.MockServerWorldProperties
import io.github.rednesto.bou.tests.lootProcessingContext
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.spongepowered.api.event.Cause
import org.spongepowered.api.event.EventContext
import org.spongepowered.math.vector.Vector3d
import java.util.*

class WorldsRequirementTests {

    @Test
    fun `single world name`() {
        assertTrue(doFulfills(listOf("world_name"), worldName = "world_name"))
    }

    @Test
    fun `several world names`() {
        assertTrue(doFulfills(listOf("world_name", "another_world_name"), worldName = "another_world_name"))
    }

    @Test
    fun `single world uuid`() {
        val worldId = UUID.randomUUID()
        assertTrue(doFulfills(listOf(worldId.toString()), worldId))
    }

    @Test
    fun `several world uuids`() {
        val worldId = UUID.randomUUID()
        assertTrue(doFulfills(listOf(worldId.toString(), UUID.randomUUID().toString()), worldId))
    }

    @Test
    fun `mixed world ids 1`() {
        val worldId = UUID.randomUUID()
        assertTrue(doFulfills(listOf("some_world", worldId.toString()), worldId))
    }

    @Test
    fun `mixed world ids 2`() {
        assertTrue(doFulfills(listOf("expected_world", UUID.randomUUID().toString()), worldName = "expected_world"))
    }

    @Test
    fun `not in world by name`() {
        assertFalse(doFulfills(listOf("expected_world"), worldName = "no_the_world"))
    }

    @Test
    fun `not in world by uuid`() {
        assertFalse(doFulfills(listOf(UUID.randomUUID().toString())))
    }

    private fun doFulfills(requirementIds: List<String>, worldId: UUID = UUID.randomUUID(), worldName: String = "world"): Boolean {
        val requirement = WorldsRequirement(requirementIds)
        val worldProps = MockServerWorldProperties(worldId, worldName)
        val mockPlayer = MockServerPlayer(location = MockServerLocation(MockServerWorld(worldProps), Vector3d.ZERO))
        val cause = Cause.of(EventContext.empty(), mockPlayer)
        val context = lootProcessingContext(emptyList(), Any(), cause)
        return requirement.fulfills(context)
    }
}
