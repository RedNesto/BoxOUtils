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
import org.spongepowered.api.registry.Registry
import org.spongepowered.api.registry.RegistryEntry
import org.spongepowered.api.registry.RegistryType
import java.util.*
import java.util.stream.Stream

open class MockRegistry<T>(val typeSupplier: ()-> RegistryType<T>) : Registry<T> {

    override fun type(): RegistryType<T> = type()

    override fun valueKey(value: T): ResourceKey = findValueKey(value).get()

    @Suppress("UNCHECKED_CAST")
    override fun findValueKey(value: T): Optional<ResourceKey> = Optional.ofNullable((value as? RegistryEntry<T>)?.key())

    override fun <V : T> findEntry(key: ResourceKey): Optional<RegistryEntry<V>> = throw NotImplementedError("MockRegistry method not implemented")

    override fun <V : T> findValue(key: ResourceKey): Optional<V> = throw NotImplementedError("MockRegistry method not implemented")

    override fun <V : T> value(key: ResourceKey): V = findValue<V>(key).get()

    override fun streamEntries(): Stream<RegistryEntry<T>> = throw NotImplementedError("MockRegistry method not implemented")

    override fun stream(): Stream<T> = throw NotImplementedError("MockRegistry method not implemented")

    override fun isDynamic(): Boolean = throw NotImplementedError("MockRegistry method not implemented")

    override fun <V : T> register(key: ResourceKey?, value: V): Optional<RegistryEntry<V>> = throw NotImplementedError("MockRegistry method not implemented")
}
