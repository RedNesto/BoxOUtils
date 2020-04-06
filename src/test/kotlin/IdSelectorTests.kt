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
package io.github.rednesto.bou.tests

import io.github.rednesto.bou.IdSelector.resolveId
import io.github.rednesto.bou.tests.framework.BouTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

class IdSelectorTests : BouTestCase(false) {

    override fun createConfigDir(): Path = Paths.get("")

    @Test
    fun `test successful queries`() {
        val availableIds = setOf("minecraft:dirt", "minecraft:*", "*")
        assertEquals("minecraft:dirt", resolveId(availableIds, "minecraft:dirt"))
        assertEquals("minecraft:*", resolveId(availableIds, "minecraft:grass"))
        assertEquals("*", resolveId(availableIds, "a_mod:some_block"))
    }

    @Test
    fun `test failing queries`() {
        assertEquals(null, resolveId(emptySet(), "minecraft:dirt"))
        assertEquals(null, resolveId(setOf("a_mod:*"), "minecraft:dirt"))
        assertEquals(null, resolveId(setOf("minecraft:grass"), "minecraft:dirt"))
        assertEquals(null, resolveId(setOf("minecraft:stone"), "a_mod:stone"))
        assertEquals(null, resolveId(setOf("minecraft:grass", "a_mod:*"), "minecraft:dirt"))
    }
}
