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
import org.spongepowered.api.Server
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.sound.music.MusicDisc
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.raid.Raid
import org.spongepowered.api.registry.RegistryHolder
import org.spongepowered.api.scheduler.ScheduledUpdateList
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.Direction
import org.spongepowered.api.util.Ticks
import org.spongepowered.api.world.BlockChangeFlag
import org.spongepowered.api.world.HeightType
import org.spongepowered.api.world.LightType
import org.spongepowered.api.world.WorldType
import org.spongepowered.api.world.biome.Biome
import org.spongepowered.api.world.border.WorldBorder
import org.spongepowered.api.world.chunk.WorldChunk
import org.spongepowered.api.world.difficulty.Difficulty
import org.spongepowered.api.world.explosion.Explosion
import org.spongepowered.api.world.generation.ChunkGenerator
import org.spongepowered.api.world.server.ChunkManager
import org.spongepowered.api.world.server.ServerLocation
import org.spongepowered.api.world.server.ServerWorld
import org.spongepowered.api.world.server.WorldTemplate
import org.spongepowered.api.world.server.storage.ServerWorldProperties
import org.spongepowered.api.world.storage.ChunkLayout
import org.spongepowered.api.world.volume.archetype.ArchetypeVolume
import org.spongepowered.api.world.volume.stream.StreamOptions
import org.spongepowered.api.world.volume.stream.VolumeStream
import org.spongepowered.api.world.weather.Weather
import org.spongepowered.api.world.weather.WeatherType
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i
import java.nio.file.Path
import java.time.Duration
import java.util.*
import java.util.function.Predicate

class MockServerWorld(
        private val properties: ServerWorldProperties = MockServerWorldProperties(),
        registryHolder: RegistryHolder = MockRegistryHolder()
) : ServerWorld, RegistryHolder by registryHolder {

    override fun isAreaAvailable(x: Int, y: Int, z: Int): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun block(x: Int, y: Int, z: Int): BlockState = throw NotImplementedError("MockServerWorld method not implemented")

    override fun fluid(x: Int, y: Int, z: Int): FluidState = throw NotImplementedError("MockServerWorld method not implemented")

    override fun highestYAt(x: Int, z: Int): Int = throw NotImplementedError("MockServerWorld method not implemented")

    override fun blockEntities(): MutableCollection<out BlockEntity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun blockEntity(x: Int, y: Int, z: Int): Optional<out BlockEntity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun <E : Any?> get(x: Int, y: Int, z: Int, key: Key<out Value<E>>?): Optional<E> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun min(): Vector3i = throw NotImplementedError("MockServerWorld method not implemented")

    override fun max(): Vector3i = throw NotImplementedError("MockServerWorld method not implemented")

    override fun contains(x: Int, y: Int, z: Int): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun <E : Any?, V : Value<E>?> getValue(x: Int, y: Int, z: Int, key: Key<V>?): Optional<V> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun supports(x: Int, y: Int, z: Int, key: Key<*>?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun keys(x: Int, y: Int, z: Int): MutableSet<Key<*>> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun getValues(x: Int, y: Int, z: Int): MutableSet<Value.Immutable<*>> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun height(type: HeightType?, x: Int, z: Int): Int = throw NotImplementedError("MockServerWorld method not implemented")

    override fun biome(x: Int, y: Int, z: Int): Biome = throw NotImplementedError("MockServerWorld method not implemented")

    override fun light(type: LightType?, x: Int, y: Int, z: Int): Int = throw NotImplementedError("MockServerWorld method not implemented")

    override fun biomeStream(min: Vector3i?, max: Vector3i?, options: StreamOptions?): VolumeStream<ServerWorld, Biome> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun blockStateStream(min: Vector3i?, max: Vector3i?, options: StreamOptions?): VolumeStream<ServerWorld, BlockState> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun players(): MutableCollection<ServerPlayer> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun entity(uuid: UUID?): Optional<Entity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun entities(): MutableCollection<out Entity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun <T : Entity?> entities(entityClass: Class<out T>?, box: AABB?, predicate: Predicate<in T>?): MutableCollection<out T> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun entities(box: AABB?, filter: Predicate<in Entity>?): MutableCollection<out Entity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun entityStream(min: Vector3i?, max: Vector3i?, options: StreamOptions?): VolumeStream<ServerWorld, Entity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun blockEntityStream(min: Vector3i?, max: Vector3i?, options: StreamOptions?): VolumeStream<ServerWorld, BlockEntity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun chunkLayout(): ChunkLayout = throw NotImplementedError("MockServerWorld method not implemented")

    override fun chunk(cx: Int, cy: Int, cz: Int): WorldChunk = throw NotImplementedError("MockServerWorld method not implemented")

    override fun chunkAtBlock(blockPosition: Vector3i?): WorldChunk = throw NotImplementedError("MockServerWorld method not implemented")

    override fun isChunkLoaded(x: Int, y: Int, z: Int, allowEmpty: Boolean): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun hasChunk(x: Int, y: Int, z: Int): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun hasChunk(position: Vector3i?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun random(): Random = throw NotImplementedError("MockServerWorld method not implemented")

    override fun worldType(): WorldType = throw NotImplementedError("MockServerWorld method not implemented")

    override fun border(): WorldBorder = throw NotImplementedError("MockServerWorld method not implemented")

    override fun setBorder(worldBorder: WorldBorder?): WorldBorder = throw NotImplementedError("MockServerWorld method not implemented")

    override fun isInBorder(entity: Entity?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun canSeeSky(x: Int, y: Int, z: Int): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun hasLiquid(x: Int, y: Int, z: Int): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun containsAnyLiquids(aabb: AABB?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun skylightSubtracted(): Int = throw NotImplementedError("MockServerWorld method not implemented")

    override fun seaLevel(): Int = throw NotImplementedError("MockServerWorld method not implemented")

    override fun isCollisionBoxesEmpty(entity: Entity?, aabb: AABB?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun isAreaLoaded(xStart: Int, yStart: Int, zStart: Int, xEnd: Int, yEnd: Int, zEnd: Int, allowEmpty: Boolean): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun setBiome(x: Int, y: Int, z: Int, biome: Biome?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun setBlock(x: Int, y: Int, z: Int, state: BlockState?, flag: BlockChangeFlag?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun removeBlock(x: Int, y: Int, z: Int): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun <E : Entity?> createEntity(type: EntityType<E>?, position: Vector3d?): E = throw NotImplementedError("MockServerWorld method not implemented")

    override fun createEntity(entityContainer: DataContainer?): Optional<Entity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun createEntity(entityContainer: DataContainer?, position: Vector3d?): Optional<Entity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun <E : Entity?> createEntityNaturally(type: EntityType<E>?, position: Vector3d?): E = throw NotImplementedError("MockServerWorld method not implemented")

    override fun spawnEntity(entity: Entity?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun spawnEntities(entities: MutableIterable<Entity>?): MutableCollection<Entity> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun addBlockEntity(x: Int, y: Int, z: Int, blockEntity: BlockEntity?) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun removeBlockEntity(x: Int, y: Int, z: Int) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun hasBlockState(x: Int, y: Int, z: Int, predicate: Predicate<in BlockState>?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun destroyBlock(pos: Vector3i?, performDrops: Boolean): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun <E : Any?> offer(x: Int, y: Int, z: Int, key: Key<out Value<E>>?, value: E): DataTransactionResult = throw NotImplementedError("MockServerWorld method not implemented")

    override fun remove(x: Int, y: Int, z: Int, key: Key<*>?): DataTransactionResult = throw NotImplementedError("MockServerWorld method not implemented")

    override fun undo(x: Int, y: Int, z: Int, result: DataTransactionResult?): DataTransactionResult = throw NotImplementedError("MockServerWorld method not implemented")

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: ValueContainer?): DataTransactionResult = throw NotImplementedError("MockServerWorld method not implemented")

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: ValueContainer?, function: MergeFunction?): DataTransactionResult = throw NotImplementedError("MockServerWorld method not implemented")

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, xFrom: Int, yFrom: Int, zFrom: Int, function: MergeFunction?): DataTransactionResult = throw NotImplementedError("MockServerWorld method not implemented")

    override fun validateRawData(x: Int, y: Int, z: Int, container: DataView?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun setRawData(x: Int, y: Int, z: Int, container: DataView?) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun scheduledBlockUpdates(): ScheduledUpdateList<BlockType> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun scheduledFluidUpdates(): ScheduledUpdateList<FluidType> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun seed(): Long = throw NotImplementedError("MockServerWorld method not implemented")

    override fun difficulty(): Difficulty = throw NotImplementedError("MockServerWorld method not implemented")

    override fun location(position: Vector3i?): ServerLocation = throw NotImplementedError("MockServerWorld method not implemented")

    override fun location(position: Vector3d?): ServerLocation = throw NotImplementedError("MockServerWorld method not implemented")

    override fun context(): Context = throw NotImplementedError("MockServerWorld method not implemented")

    override fun sendWorldType(worldType: WorldType?) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun spawnParticles(particleEffect: ParticleEffect?, position: Vector3d?, radius: Int) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun playMusicDisc(position: Vector3i?, musicDiscType: MusicDisc?) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun stopMusicDisc(position: Vector3i?) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun sendBlockChange(x: Int, y: Int, z: Int, state: BlockState?) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun resetBlockChange(x: Int, y: Int, z: Int) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun createArchetypeVolume(min: Vector3i?, max: Vector3i?, origin: Vector3i?): ArchetypeVolume = throw NotImplementedError("MockServerWorld method not implemented")

    override fun weather(): Weather = throw NotImplementedError("MockServerWorld method not implemented")

    override fun engine(): Server = throw NotImplementedError("MockServerWorld method not implemented")

    override fun properties(): ServerWorldProperties = properties

    override fun isLoaded(): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun closestPlayer(x: Int, y: Int, z: Int, distance: Double, predicate: Predicate<in Player>?): Optional<out Player> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun loadChunk(cx: Int, cy: Int, cz: Int, shouldGenerate: Boolean): Optional<WorldChunk> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun loadedChunks(): MutableIterable<WorldChunk> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun hitBlock(x: Int, y: Int, z: Int, side: Direction?, profile: GameProfile?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun interactBlock(x: Int, y: Int, z: Int, side: Direction?, profile: GameProfile?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun interactBlockWith(x: Int, y: Int, z: Int, itemStack: ItemStack?, side: Direction?, profile: GameProfile?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun placeBlock(x: Int, y: Int, z: Int, block: BlockState?, side: Direction?, profile: GameProfile?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun digBlock(x: Int, y: Int, z: Int, profile: GameProfile?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun digBlockWith(x: Int, y: Int, z: Int, itemStack: ItemStack?, profile: GameProfile?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun blockDigTimeWith(x: Int, y: Int, z: Int, itemStack: ItemStack?, profile: GameProfile?): Duration = throw NotImplementedError("MockServerWorld method not implemented")

    override fun setWeather(weather: WeatherType?) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun setWeather(weather: WeatherType?, ticks: Ticks?) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun generator(): ChunkGenerator = throw NotImplementedError("MockServerWorld method not implemented")

    override fun asTemplate(): WorldTemplate = throw NotImplementedError("MockServerWorld method not implemented")

    override fun key(): ResourceKey = throw NotImplementedError("MockServerWorld method not implemented")

    override fun createSnapshot(x: Int, y: Int, z: Int): BlockSnapshot = throw NotImplementedError("MockServerWorld method not implemented")

    override fun restoreSnapshot(snapshot: BlockSnapshot?, force: Boolean, flag: BlockChangeFlag?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun restoreSnapshot(x: Int, y: Int, z: Int, snapshot: BlockSnapshot?, force: Boolean, flag: BlockChangeFlag?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun directory(): Path = throw NotImplementedError("MockServerWorld method not implemented")

    override fun save(): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun unloadChunk(chunk: WorldChunk?): Boolean = throw NotImplementedError("MockServerWorld method not implemented")

    override fun triggerExplosion(explosion: Explosion?) = throw NotImplementedError("MockServerWorld method not implemented")

    override fun raids(): MutableCollection<Raid> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun raidAt(blockPosition: Vector3i?): Optional<Raid> = throw NotImplementedError("MockServerWorld method not implemented")

    override fun chunkManager(): ChunkManager = throw NotImplementedError("MockServerWorld method not implemented")
}
