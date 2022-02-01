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
import org.spongepowered.api.registry.DefaultedRegistryType
import org.spongepowered.api.registry.Registry
import org.spongepowered.api.registry.RegistryHolder
import org.spongepowered.api.registry.RegistryType
import java.util.*
import java.util.function.Supplier

class MockRegistryType<T>(val root: ResourceKey, val location: ResourceKey) : RegistryType<T> {
    override fun root(): ResourceKey = root

    override fun location(): ResourceKey = location

    override fun <V : T> asDefaultedType(defaultHolder: Supplier<RegistryHolder>): DefaultedRegistryType<V> =
            MockDefaultedRegistryType(root, location, defaultHolder)
}

class MockDefaultedRegistryType<T>(
        val root: ResourceKey,
        val location: ResourceKey,
        val defaultHolder: Supplier<RegistryHolder>
) : DefaultedRegistryType<T> {
    override fun root(): ResourceKey = root

    override fun location(): ResourceKey = location

    override fun <V : T> asDefaultedType(defaultHolder: Supplier<RegistryHolder>): DefaultedRegistryType<V> =
            MockDefaultedRegistryType(root, location, defaultHolder)

    override fun get(): Registry<T> = defaultHolder.get().registry(this)

    override fun find(): Optional<Registry<T>> = defaultHolder.get().findRegistry(this)

    override fun defaultHolder(): Supplier<RegistryHolder> = defaultHolder
}

object MockRegistryTypeFactory : RegistryType.Factory {
    override fun <T : Any?> create(root: ResourceKey, location: ResourceKey): RegistryType<T> {
        return MockRegistryType(root, location)
    }
}
