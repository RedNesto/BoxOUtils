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
import org.spongepowered.api.registry.RegistryHolder
import org.spongepowered.api.registry.RegistryType
import java.util.*
import java.util.stream.Stream

class MockRegistryHolder(val registries: Map<ResourceKey, Registry<*>>) : RegistryHolder {

    constructor() : this(mapOf(
            id("minecraft:item") to MockItemTypeRegistry,
            id("minecraft:enchantment") to MockEnchantmentTypeRegistry
    ))

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> registry(type: RegistryType<T>): Registry<T> = registries[type.location()] as Registry<T>

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> findRegistry(type: RegistryType<T>): Optional<Registry<T>> =
            Optional.ofNullable(registries.getOrDefault(type.location(), null)) as Optional<Registry<T>>

    override fun streamRegistries(root: ResourceKey?): Stream<Registry<*>> = throw NotImplementedError("MockRegistryHolder method not implemented")
}
