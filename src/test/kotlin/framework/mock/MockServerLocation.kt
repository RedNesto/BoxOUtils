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
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.value.*
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.scheduler.ScheduledUpdate
import org.spongepowered.api.scheduler.TaskPriority
import org.spongepowered.api.util.Direction
import org.spongepowered.api.util.Ticks
import org.spongepowered.api.world.BlockChangeFlag
import org.spongepowered.api.world.LocatableBlock
import org.spongepowered.api.world.biome.Biome
import org.spongepowered.api.world.server.ServerLocation
import org.spongepowered.api.world.server.ServerWorld
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i
import java.time.Duration
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.function.BiFunction

class MockServerLocation(val world: MockServerWorld, val pos: Vector3d) : ServerLocation {

    override fun <E : Any?> get(key: Key<out Value<E>>?): Optional<E> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <E : Any?> get(direction: Direction?, key: Key<out Value<E>>?): Optional<E> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <E : Any?, V : Value<E>?> getValue(key: Key<V>?): Optional<V> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun supports(key: Key<*>?): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun getKeys(): MutableSet<Key<*>> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun getValues(): MutableSet<Value.Immutable<*>> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <E : Any?> offer(key: Key<out Value<E>>?, value: E): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun offer(value: Value<*>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <E : Any?> offerSingle(key: Key<out CollectionValue<E, *>>?, element: E): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <K : Any?, V : Any?> offerSingle(key: Key<out MapValue<K, V>>?, valueKey: K, value: V): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <K : Any?, V : Any?> offerAll(key: Key<out MapValue<K, V>>?, map: MutableMap<out K, out V>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun offerAll(value: MapValue<*, *>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun offerAll(value: CollectionValue<*, *>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <E : Any?> offerAll(key: Key<out CollectionValue<E, *>>?, elements: MutableCollection<out E>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <E : Any?> removeSingle(key: Key<out CollectionValue<E, *>>?, element: E): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <K : Any?> removeKey(key: Key<out MapValue<K, *>>?, mapKey: K): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun removeAll(value: CollectionValue<*, *>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <E : Any?> removeAll(key: Key<out CollectionValue<E, *>>?, elements: MutableCollection<out E>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun removeAll(value: MapValue<*, *>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <K : Any?, V : Any?> removeAll(key: Key<out MapValue<K, V>>?, map: MutableMap<out K, out V>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <E : Any?> tryOffer(key: Key<out Value<E>>?, value: E): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun remove(key: Key<*>?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun undo(result: DataTransactionResult?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun copyFrom(that: ValueContainer?, function: MergeFunction?): DataTransactionResult = throw NotImplementedError("MockServerLocation method not implemented")

    override fun world(): ServerWorld = world

    override fun worldIfAvailable(): Optional<ServerWorld> = Optional.of(world)

    override fun isAvailable(): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun isValid(): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun position(): Vector3d = pos

    override fun blockPosition(): Vector3i = pos.toInt()

    override fun chunkPosition(): Vector3i = throw NotImplementedError("MockServerLocation method not implemented")

    override fun biomePosition(): Vector3i = throw NotImplementedError("MockServerLocation method not implemented")

    override fun x(): Double = throw NotImplementedError("MockServerLocation method not implemented")

    override fun y(): Double = throw NotImplementedError("MockServerLocation method not implemented")

    override fun z(): Double = throw NotImplementedError("MockServerLocation method not implemented")

    override fun blockX(): Int = throw NotImplementedError("MockServerLocation method not implemented")

    override fun blockY(): Int = throw NotImplementedError("MockServerLocation method not implemented")

    override fun blockZ(): Int = throw NotImplementedError("MockServerLocation method not implemented")

    override fun inWorld(world: ServerWorld?): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun withWorld(world: ServerWorld?): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun withPosition(position: Vector3d?): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun withBlockPosition(position: Vector3i?): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun sub(v: Vector3d?): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun sub(v: Vector3i?): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun sub(x: Double, y: Double, z: Double): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun add(v: Vector3d?): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun add(v: Vector3i?): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun add(x: Double, y: Double, z: Double): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun relativeTo(direction: Direction?): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun relativeToBlock(direction: Direction?): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun biome(): Biome = throw NotImplementedError("MockServerLocation method not implemented")

    override fun hasBlock(): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun block(): BlockState = throw NotImplementedError("MockServerLocation method not implemented")

    override fun fluid(): FluidState = throw NotImplementedError("MockServerLocation method not implemented")

    override fun hasBlockEntity(): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun blockEntity(): Optional<out BlockEntity> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun setBlock(state: BlockState?): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun setBlock(state: BlockState?, flag: BlockChangeFlag?): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun setBlockType(type: BlockType?): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun setBlockType(type: BlockType?, flag: BlockChangeFlag?): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun contentVersion(): Int = throw NotImplementedError("MockServerLocation method not implemented")

    override fun toContainer(): DataContainer = throw NotImplementedError("MockServerLocation method not implemented")

    override fun worldKey(): ResourceKey = throw NotImplementedError("MockServerLocation method not implemented")

    override fun asLocatableBlock(): LocatableBlock = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <T : Any?> map(mapper: BiFunction<ServerWorld, Vector3d, T>?): T = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <T : Any?> mapBlock(mapper: BiFunction<ServerWorld, Vector3i, T>?): T = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <T : Any?> mapChunk(mapper: BiFunction<ServerWorld, Vector3i, T>?): T = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <T : Any?> mapBiome(mapper: BiFunction<ServerWorld, Vector3i, T>?): T = throw NotImplementedError("MockServerLocation method not implemented")

    override fun restoreSnapshot(snapshot: BlockSnapshot?, force: Boolean, flag: BlockChangeFlag?): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun removeBlock(): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun <E : Entity?> createEntity(type: EntityType<E>?): E = throw NotImplementedError("MockServerLocation method not implemented")

    override fun spawnEntity(entity: Entity?): Boolean = throw NotImplementedError("MockServerLocation method not implemented")

    override fun spawnEntities(entities: MutableIterable<Entity>?): MutableCollection<Entity> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun asHighestLocation(): ServerLocation = throw NotImplementedError("MockServerLocation method not implemented")

    override fun createSnapshot(): BlockSnapshot = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduledBlockUpdates(): MutableCollection<out ScheduledUpdate<BlockType>> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleBlockUpdate(delay: Int, temporalUnit: TemporalUnit?): ScheduledUpdate<BlockType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleBlockUpdate(delay: Ticks?, priority: TaskPriority?): ScheduledUpdate<BlockType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleBlockUpdate(delay: Int, temporalUnit: TemporalUnit?, priority: TaskPriority?): ScheduledUpdate<BlockType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleBlockUpdate(delay: Ticks?): ScheduledUpdate<BlockType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleBlockUpdate(delay: Duration?): ScheduledUpdate<BlockType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleBlockUpdate(delay: Duration?, priority: TaskPriority?): ScheduledUpdate<BlockType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduledFluidUpdates(): MutableCollection<out ScheduledUpdate<FluidType>> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleFluidUpdate(delay: Int, temporalUnit: TemporalUnit?): ScheduledUpdate<FluidType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleFluidUpdate(delay: Int, temporalUnit: TemporalUnit?, priority: TaskPriority?): ScheduledUpdate<FluidType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleFluidUpdate(ticks: Ticks?): ScheduledUpdate<FluidType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleFluidUpdate(ticks: Ticks?, priority: TaskPriority?): ScheduledUpdate<FluidType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleFluidUpdate(delay: Duration?): ScheduledUpdate<FluidType> = throw NotImplementedError("MockServerLocation method not implemented")

    override fun scheduleFluidUpdate(delay: Duration?, priority: TaskPriority?): ScheduledUpdate<FluidType> = throw NotImplementedError("MockServerLocation method not implemented")
}
