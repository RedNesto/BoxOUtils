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
package io.github.rednesto.bou.tests.framework.mock

import org.spongepowered.api.ResourceKey
import org.spongepowered.plugin.PluginContainer

fun id(namespace: String, value: String): ResourceKey = MockResourceKey(namespace, value)

fun id(id: String): ResourceKey {
    val split = id.split(':')
    if (split.size == 1) {
        return MockResourceKey("minecraft", split[0])
    }
    return MockResourceKey(split[0], split[1])
}

data class MockResourceKey(val ns: String, val v: String) : ResourceKey {
    override fun namespace(): String = ns

    override fun value(): String = v
}

object ResourceKeyFactory : ResourceKey.Factory {
    override fun of(namespace: String, value: String): ResourceKey = id(namespace, value)

    override fun of(plugin: PluginContainer, value: String): ResourceKey = id(plugin.metadata().id(), value)

    override fun resolve(formatted: String): ResourceKey = id(formatted)
}
