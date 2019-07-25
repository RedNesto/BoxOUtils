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

import com.flowpowered.math.vector.Vector3d
import com.flowpowered.math.vector.Vector3i
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.ScheduledBlockUpdate
import org.spongepowered.api.block.tileentity.TileEntity
import org.spongepowered.api.data.*
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.manipulator.DataManipulator
import org.spongepowered.api.data.merge.MergeFunction
import org.spongepowered.api.data.value.BaseValue
import org.spongepowered.api.data.value.immutable.ImmutableValue
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.sound.SoundCategory
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.effect.sound.record.RecordType
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntitySnapshot
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.text.BookView
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.chat.ChatType
import org.spongepowered.api.text.title.Title
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.Direction
import org.spongepowered.api.util.DiscreteTransform3
import org.spongepowered.api.world.*
import org.spongepowered.api.world.biome.BiomeType
import org.spongepowered.api.world.explosion.Explosion
import org.spongepowered.api.world.extent.*
import org.spongepowered.api.world.extent.worker.MutableBiomeVolumeWorker
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker
import org.spongepowered.api.world.gen.WorldGenerator
import org.spongepowered.api.world.storage.WorldProperties
import org.spongepowered.api.world.storage.WorldStorage
import org.spongepowered.api.world.weather.Weather
import java.nio.file.Path
import java.util.*
import java.util.function.Predicate

class MockWorld(private val uniqueId: UUID = UUID.randomUUID(), private val worldName: String = "test") : World {
    override fun getUniqueId(): UUID = uniqueId

    override fun getName(): String  = worldName

    override fun interactBlockWith(x: Int, y: Int, z: Int, itemStack: ItemStack, side: Direction, profile: GameProfile): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getIntersectingEntities(box: AABB, filter: Predicate<Entity>): MutableSet<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getIntersectingEntities(start: Vector3d, end: Vector3d, filter: Predicate<EntityUniverse.EntityHit>): MutableSet<EntityUniverse.EntityHit> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getIntersectingEntities(start: Vector3d, direction: Vector3d, distance: Double, filter: Predicate<EntityUniverse.EntityHit>): MutableSet<EntityUniverse.EntityHit> = throw NotImplementedError("MockPlayer method not implemented")

    override fun setViewDistance(viewDistance: Int) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockMin(): Vector3i = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockView(newMin: Vector3i, newMax: Vector3i): MutableBlockVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockView(transform: DiscreteTransform3): MutableBlockVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun <E : Any?> offer(x: Int, y: Int, z: Int, key: Key<out BaseValue<E>>, value: E): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun offer(x: Int, y: Int, z: Int, manipulator: DataManipulator<*, *>, function: MergeFunction): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun spawnEntity(entity: Entity): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getTileEntity(x: Int, y: Int, z: Int): Optional<TileEntity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBiome(x: Int, y: Int, z: Int): BiomeType = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendBlockChange(x: Int, y: Int, z: Int, state: BlockState) = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendMessage(message: Text) = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendMessage(type: ChatType, message: Text) = throw NotImplementedError("MockPlayer method not implemented")

    override fun triggerExplosion(explosion: Explosion) = throw NotImplementedError("MockPlayer method not implemented")

    override fun setWeather(weather: Weather) = throw NotImplementedError("MockPlayer method not implemented")

    override fun setWeather(weather: Weather, duration: Long) = throw NotImplementedError("MockPlayer method not implemented")

    override fun interactBlock(x: Int, y: Int, z: Int, side: Direction, profile: GameProfile): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getMessageChannel(): MessageChannel = throw NotImplementedError("MockPlayer method not implemented")

    override fun getFacesWithProperty(x: Int, y: Int, z: Int, propertyClass: Class<out Property<*, *>>): MutableCollection<Direction> = throw NotImplementedError("MockPlayer method not implemented")

    override fun createSnapshot(x: Int, y: Int, z: Int): BlockSnapshot = throw NotImplementedError("MockPlayer method not implemented")

    override fun supports(x: Int, y: Int, z: Int, key: Key<*>): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun supports(x: Int, y: Int, z: Int, manipulatorClass: Class<out DataManipulator<*, *>>): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getUnmodifiableBlockView(): UnmodifiableBlockVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun resetViewDistance() = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBiomeCopy(type: StorageType): MutableBiomeVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockType(x: Int, y: Int, z: Int): BlockType = throw NotImplementedError("MockPlayer method not implemented")

    override fun undo(x: Int, y: Int, z: Int, result: DataTransactionResult): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun playSound(sound: SoundType, category: SoundCategory, position: Vector3d, volume: Double) = throw NotImplementedError("MockPlayer method not implemented")

    override fun playSound(sound: SoundType, category: SoundCategory, position: Vector3d, volume: Double, pitch: Double) = throw NotImplementedError("MockPlayer method not implemented")

    override fun playSound(sound: SoundType, category: SoundCategory, position: Vector3d, volume: Double, pitch: Double, minVolume: Double) = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopSounds() = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopSounds(sound: SoundType) = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopSounds(category: SoundCategory) = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopSounds(sound: SoundType, category: SoundCategory) = throw NotImplementedError("MockPlayer method not implemented")

    override fun createArchetypeVolume(min: Vector3i, max: Vector3i, origin: Vector3i): ArchetypeVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun getScheduledUpdates(x: Int, y: Int, z: Int): MutableCollection<ScheduledBlockUpdate> = throw NotImplementedError("MockPlayer method not implemented")

    override fun digBlock(x: Int, y: Int, z: Int, profile: GameProfile): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBiomeWorker(): MutableBiomeVolumeWorker<World> = throw NotImplementedError("MockPlayer method not implemented")

    override fun spawnEntities(entities: MutableIterable<Entity>): MutableCollection<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getProperties(): WorldProperties = throw NotImplementedError("MockPlayer method not implemented")

    override fun getProperties(x: Int, y: Int, z: Int): MutableCollection<Property<*, *>> = throw NotImplementedError("MockPlayer method not implemented")

    override fun <T : Property<*, *>?> getProperty(x: Int, y: Int, z: Int, propertyClass: Class<T>): Optional<T> = throw NotImplementedError("MockPlayer method not implemented")

    override fun <T : Property<*, *>?> getProperty(x: Int, y: Int, z: Int, direction: Direction, propertyClass: Class<T>): Optional<T> = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendTitle(title: Title) = throw NotImplementedError("MockPlayer method not implemented")

    override fun <E : Any?, V : BaseValue<E>?> getValue(x: Int, y: Int, z: Int, key: Key<V>): Optional<V> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getPortalAgent(): PortalAgent = throw NotImplementedError("MockPlayer method not implemented")

    override fun <E : Any?> get(x: Int, y: Int, z: Int, key: Key<out BaseValue<E>>): Optional<E> = throw NotImplementedError("MockPlayer method not implemented")

    override fun <T : DataManipulator<*, *>?> get(x: Int, y: Int, z: Int, manipulatorClass: Class<T>): Optional<T> = throw NotImplementedError("MockPlayer method not implemented")

    override fun addScheduledUpdate(x: Int, y: Int, z: Int, priority: Int, ticks: Int): ScheduledBlockUpdate = throw NotImplementedError("MockPlayer method not implemented")

    override fun placeBlock(x: Int, y: Int, z: Int, block: BlockState, side: Direction, profile: GameProfile): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBiomeSize(): Vector3i = throw NotImplementedError("MockPlayer method not implemented")

    override fun getImmutableBlockCopy(): ImmutableBlockVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockDigTimeWith(x: Int, y: Int, z: Int, itemStack: ItemStack, profile: GameProfile): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun containsBiome(x: Int, y: Int, z: Int): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getKeys(x: Int, y: Int, z: Int): MutableSet<Key<*>> = throw NotImplementedError("MockPlayer method not implemented")

    override fun resetBlockChange(x: Int, y: Int, z: Int) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBiomeView(newMin: Vector3i, newMax: Vector3i): MutableBiomeVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBiomeView(transform: DiscreteTransform3): MutableBiomeVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendBookView(bookView: BookView) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getRunningDuration(): Long = throw NotImplementedError("MockPlayer method not implemented")

    override fun containsBlock(x: Int, y: Int, z: Int): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun removeScheduledUpdate(x: Int, y: Int, z: Int, update: ScheduledBlockUpdate) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getValues(x: Int, y: Int, z: Int): MutableSet<ImmutableValue<*>> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getWeather(): Weather = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBiomeMin(): Vector3i = throw NotImplementedError("MockPlayer method not implemented")

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: DataHolder): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: DataHolder, function: MergeFunction): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, xFrom: Int, yFrom: Int, zFrom: Int, function: MergeFunction): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlock(x: Int, y: Int, z: Int): BlockState = throw NotImplementedError("MockPlayer method not implemented")

    override fun setMessageChannel(channel: MessageChannel) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getImmutableBiomeCopy(): ImmutableBiomeVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun <T : DataManipulator<*, *>?> getOrCreate(x: Int, y: Int, z: Int, manipulatorClass: Class<T>): Optional<T> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getDirectory(): Path = throw NotImplementedError("MockPlayer method not implemented")

    override fun getManipulators(x: Int, y: Int, z: Int): MutableCollection<DataManipulator<*, *>> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getCreator(x: Int, y: Int, z: Int): Optional<UUID> = throw NotImplementedError("MockPlayer method not implemented")

    override fun isLoaded(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun digBlockWith(x: Int, y: Int, z: Int, itemStack: ItemStack, profile: GameProfile): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getIntersectingCollisionBoxes(owner: Entity, box: AABB): MutableSet<AABB> = throw NotImplementedError("MockPlayer method not implemented")

    override fun newChunkPreGenerate(center: Vector3d, diameter: Double): ChunkPreGenerate.Builder = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockWorker(): MutableBlockVolumeWorker<World> = throw NotImplementedError("MockPlayer method not implemented")

    override fun loadChunk(cx: Int, cy: Int, cz: Int, shouldGenerate: Boolean): Optional<Chunk> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getExtentView(newMin: Vector3i, newMax: Vector3i): Extent = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockSelectionBox(x: Int, y: Int, z: Int): Optional<AABB> = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopRecord(position: Vector3i) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getNotifier(x: Int, y: Int, z: Int): Optional<UUID> = throw NotImplementedError("MockPlayer method not implemented")

    override fun save(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getViewDistance(): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun getDimension(): Dimension = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBiomeMax(): Vector3i = throw NotImplementedError("MockPlayer method not implemented")

    override fun getWorldBorder(): WorldBorder = throw NotImplementedError("MockPlayer method not implemented")

    override fun getEntity(uuid: UUID): Optional<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun spawnParticles(particleEffect: ParticleEffect, position: Vector3d) = throw NotImplementedError("MockPlayer method not implemented")

    override fun spawnParticles(particleEffect: ParticleEffect, position: Vector3d, radius: Int) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getContext(): Context = throw NotImplementedError("MockPlayer method not implemented")

    override fun unloadChunk(chunk: Chunk): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getPrecipitationLevelAt(x: Int, z: Int): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun setBlock(x: Int, y: Int, z: Int, blockState: BlockState, flag: BlockChangeFlag): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockState): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getWorldGenerator(): WorldGenerator = throw NotImplementedError("MockPlayer method not implemented")

    override fun getSeaLevel(): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun setRawData(x: Int, y: Int, z: Int, container: DataView) = throw NotImplementedError("MockPlayer method not implemented")

    override fun setNotifier(x: Int, y: Int, z: Int, uuid: UUID?) = throw NotImplementedError("MockPlayer method not implemented")

    override fun createEntityNaturally(type: EntityType, position: Vector3d): Entity = throw NotImplementedError("MockPlayer method not implemented")

    override fun setCreator(x: Int, y: Int, z: Int, uuid: UUID?) = throw NotImplementedError("MockPlayer method not implemented")

    override fun hitBlock(x: Int, y: Int, z: Int, side: Direction, profile: GameProfile): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getLoadedChunks(): MutableIterable<Chunk> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockSize(): Vector3i = throw NotImplementedError("MockPlayer method not implemented")

    override fun getTileEntities(): MutableCollection<TileEntity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getTileEntities(filter: Predicate<TileEntity>): MutableCollection<TileEntity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getRemainingDuration(): Long = throw NotImplementedError("MockPlayer method not implemented")

    override fun getIntersectingBlockCollisionBoxes(box: AABB): MutableSet<AABB> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getPlayers(): MutableCollection<Player> = throw NotImplementedError("MockPlayer method not implemented")

    override fun playRecord(position: Vector3i, recordType: RecordType) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getChunk(cx: Int, cy: Int, cz: Int): Optional<Chunk> = throw NotImplementedError("MockPlayer method not implemented")

    override fun validateRawData(x: Int, y: Int, z: Int, container: DataView): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun setBiome(x: Int, y: Int, z: Int, biome: BiomeType) = throw NotImplementedError("MockPlayer method not implemented")

    override fun createEntity(type: EntityType, position: Vector3d): Entity = throw NotImplementedError("MockPlayer method not implemented")

    override fun createEntity(entityContainer: DataContainer): Optional<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun createEntity(entityContainer: DataContainer, position: Vector3d): Optional<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun remove(x: Int, y: Int, z: Int, manipulatorClass: Class<out DataManipulator<*, *>>): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun remove(x: Int, y: Int, z: Int, key: Key<*>): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun getEntities(): MutableCollection<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getEntities(filter: Predicate<Entity>): MutableCollection<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getUnmodifiableBiomeView(): UnmodifiableBiomeVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun getWorldStorage(): WorldStorage = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockCopy(type: StorageType): MutableBlockVolume = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBlockMax(): Vector3i = throw NotImplementedError("MockPlayer method not implemented")

    override fun getHighestYAt(x: Int, z: Int): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun restoreSnapshot(snapshot: BlockSnapshot, force: Boolean, flag: BlockChangeFlag): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun restoreSnapshot(x: Int, y: Int, z: Int, snapshot: BlockSnapshot, force: Boolean, flag: BlockChangeFlag): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun restoreSnapshot(snapshot: EntitySnapshot, position: Vector3d): Optional<Entity> = throw NotImplementedError("MockPlayer method not implemented")
}
