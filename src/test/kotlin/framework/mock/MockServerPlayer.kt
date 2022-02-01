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

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementProgress
import org.spongepowered.api.advancement.AdvancementTree
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.value.*
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.sound.music.MusicDisc
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityArchetype
import org.spongepowered.api.entity.EntitySnapshot
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.attribute.Attribute
import org.spongepowered.api.entity.attribute.type.AttributeType
import org.spongepowered.api.entity.living.player.CooldownTracker
import org.spongepowered.api.entity.living.player.PlayerChatFormatter
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.entity.living.player.tab.TabList
import org.spongepowered.api.entity.projectile.Projectile
import org.spongepowered.api.event.Cause
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.message.PlayerChatEvent
import org.spongepowered.api.item.inventory.Container
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.entity.PlayerInventory
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.network.ServerPlayerConnection
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.resourcepack.ResourcePack
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.RelativePositions
import org.spongepowered.api.util.Transform
import org.spongepowered.api.util.Tristate
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.WorldType
import org.spongepowered.api.world.border.WorldBorder
import org.spongepowered.api.world.server.ServerLocation
import org.spongepowered.api.world.server.ServerWorld
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i
import java.util.*
import java.util.function.UnaryOperator

class MockServerPlayer(
        private val permissions: List<String> = listOf(),
        private val location: ServerLocation = MockServerLocation(MockServerWorld(), Vector3d.ZERO)
) : ServerPlayer {

    override fun attribute(type: AttributeType?): Optional<Attribute> = throw NotImplementedError("MockServer player method not implemented")

    override fun uniqueId(): UUID = throw NotImplementedError("MockServer player method not implemented")

    override fun asHoverEvent(op: UnaryOperator<HoverEvent.ShowEntity>): HoverEvent<HoverEvent.ShowEntity> = throw NotImplementedError("MockServer player method not implemented")

    override fun world(): ServerWorld = location.world()

    override fun location(): Location<*, *> = location

    override fun <T : Projectile?> launchProjectile(projectileType: EntityType<T>?): Optional<T> = throw NotImplementedError("MockServer player method not implemented")

    override fun <T : Projectile?> launchProjectile(projectileType: EntityType<T>?, velocity: Vector3d?): Optional<T> = throw NotImplementedError("MockServer player method not implemented")

    override fun <T : Projectile?> launchProjectileTo(projectileType: EntityType<T>?, target: Entity?): Optional<T> = throw NotImplementedError("MockServer player method not implemented")

    override fun contentVersion(): Int = throw NotImplementedError("MockServer player method not implemented")

    override fun toContainer(): DataContainer = throw NotImplementedError("MockServer player method not implemented")

    override fun <E : Any?> get(key: Key<out Value<E>>?): Optional<E> = throw NotImplementedError("MockServer player method not implemented")

    override fun <E : Any?, V : Value<E>?> getValue(key: Key<V>?): Optional<V> = throw NotImplementedError("MockServer player method not implemented")

    override fun supports(key: Key<*>?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun getKeys(): MutableSet<Key<*>> = throw NotImplementedError("MockServer player method not implemented")

    override fun getValues(): MutableSet<Value.Immutable<*>> = throw NotImplementedError("MockServer player method not implemented")

    override fun copy(): Entity = throw NotImplementedError("MockServer player method not implemented")

    override fun validateRawData(container: DataView?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun <E : Any?> offer(key: Key<out Value<E>>?, value: E): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun offer(value: Value<*>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun <E : Any?> offerSingle(key: Key<out CollectionValue<E, *>>?, element: E): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun <K : Any?, V : Any?> offerSingle(key: Key<out MapValue<K, V>>?, valueKey: K, value: V): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun <K : Any?, V : Any?> offerAll(key: Key<out MapValue<K, V>>?, map: MutableMap<out K, out V>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun offerAll(value: MapValue<*, *>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun offerAll(value: CollectionValue<*, *>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun <E : Any?> offerAll(key: Key<out CollectionValue<E, *>>?, elements: MutableCollection<out E>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun <E : Any?> removeSingle(key: Key<out CollectionValue<E, *>>?, element: E): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun <K : Any?> removeKey(key: Key<out MapValue<K, *>>?, mapKey: K): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun removeAll(value: CollectionValue<*, *>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun <E : Any?> removeAll(key: Key<out CollectionValue<E, *>>?, elements: MutableCollection<out E>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun removeAll(value: MapValue<*, *>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun <K : Any?, V : Any?> removeAll(key: Key<out MapValue<K, V>>?, map: MutableMap<out K, out V>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun <E : Any?> tryOffer(key: Key<out Value<E>>?, value: E): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun remove() = throw NotImplementedError("MockServer player method not implemented")

    override fun remove(key: Key<*>?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun undo(result: DataTransactionResult?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun copyFrom(that: ValueContainer?, function: MergeFunction?): DataTransactionResult = throw NotImplementedError("MockServer player method not implemented")

    override fun setRawData(container: DataView?) = throw NotImplementedError("MockServer player method not implemented")

    override fun random(): Random = throw NotImplementedError("MockServer player method not implemented")

    override fun type(): EntityType<*> = throw NotImplementedError("MockServer player method not implemented")

    override fun createSnapshot(): EntitySnapshot = throw NotImplementedError("MockServer player method not implemented")

    override fun createArchetype(): EntityArchetype = throw NotImplementedError("MockServer player method not implemented")

    override fun position(): Vector3d = throw NotImplementedError("MockServer player method not implemented")

    override fun setPosition(position: Vector3d?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun setLocation(location: ServerLocation?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun rotation(): Vector3d = throw NotImplementedError("MockServer player method not implemented")

    override fun setRotation(rotation: Vector3d?) = throw NotImplementedError("MockServer player method not implemented")

    override fun setLocationAndRotation(location: ServerLocation?, rotation: Vector3d?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun setLocationAndRotation(location: ServerLocation?, rotation: Vector3d?, relativePositions: EnumSet<RelativePositions>?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun scale(): Vector3d = throw NotImplementedError("MockServer player method not implemented")

    override fun setScale(scale: Vector3d?) = throw NotImplementedError("MockServer player method not implemented")

    override fun transform(): Transform = throw NotImplementedError("MockServer player method not implemented")

    override fun setTransform(transform: Transform?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun transferToWorld(world: ServerWorld?, position: Vector3d?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun boundingBox(): Optional<AABB> = throw NotImplementedError("MockServer player method not implemented")

    override fun isRemoved(): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun isLoaded(): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun damage(damage: Double, damageSource: DamageSource?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun teamRepresentation(): Component = throw NotImplementedError("MockServer player method not implemented")

    override fun lookAt(targetPos: Vector3d?) = throw NotImplementedError("MockServer player method not implemented")

    override fun equipment(): EquipmentInventory = throw NotImplementedError("MockServer player method not implemented")

    override fun canEquip(type: EquipmentType?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun canEquip(type: EquipmentType?, equipment: ItemStack?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun equipped(type: EquipmentType?): Optional<ItemStack> = throw NotImplementedError("MockServer player method not implemented")

    override fun equip(type: EquipmentType?, equipment: ItemStack?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun head(): ItemStack = throw NotImplementedError("MockServer player method not implemented")

    override fun setHead(head: ItemStack?) = throw NotImplementedError("MockServer player method not implemented")

    override fun chest(): ItemStack = throw NotImplementedError("MockServer player method not implemented")

    override fun setChest(chest: ItemStack?) = throw NotImplementedError("MockServer player method not implemented")

    override fun legs(): ItemStack = throw NotImplementedError("MockServer player method not implemented")

    override fun setLegs(legs: ItemStack?) = throw NotImplementedError("MockServer player method not implemented")

    override fun feet(): ItemStack = throw NotImplementedError("MockServer player method not implemented")

    override fun setFeet(feet: ItemStack?) = throw NotImplementedError("MockServer player method not implemented")

    override fun itemInHand(handType: HandType?): ItemStack = throw NotImplementedError("MockServer player method not implemented")

    override fun setItemInHand(handType: HandType?, itemInHand: ItemStack?) = throw NotImplementedError("MockServer player method not implemented")

    override fun name(): String = throw NotImplementedError("MockServer player method not implemented")

    override fun sendWorldType(worldType: WorldType?) = throw NotImplementedError("MockServer player method not implemented")

    override fun spawnParticles(particleEffect: ParticleEffect?, position: Vector3d?, radius: Int) = throw NotImplementedError("MockServer player method not implemented")

    override fun playMusicDisc(position: Vector3i?, musicDiscType: MusicDisc?) = throw NotImplementedError("MockServer player method not implemented")

    override fun stopMusicDisc(position: Vector3i?) = throw NotImplementedError("MockServer player method not implemented")

    override fun sendBlockChange(x: Int, y: Int, z: Int, state: BlockState?) = throw NotImplementedError("MockServer player method not implemented")

    override fun resetBlockChange(x: Int, y: Int, z: Int) = throw NotImplementedError("MockServer player method not implemented")

    override fun inventory(): PlayerInventory = throw NotImplementedError("MockServer player method not implemented")

    override fun profile(): GameProfile = throw NotImplementedError("MockServer player method not implemented")

    override fun enderChestInventory(): Inventory = throw NotImplementedError("MockServer player method not implemented")

    override fun identifier(): String = throw NotImplementedError("MockServer player method not implemented")

    override fun containingCollection(): SubjectCollection = throw NotImplementedError("MockServer player method not implemented")

    override fun asSubjectReference(): SubjectReference = throw NotImplementedError("MockServer player method not implemented")

    override fun associatedObject(): Optional<Any> = Optional.empty()

    override fun isSubjectDataPersisted(): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun subjectData(): SubjectData = throw NotImplementedError("MockServer player method not implemented")

    override fun transientSubjectData(): SubjectData = throw NotImplementedError("MockServer player method not implemented")

    override fun permissionValue(permission: String?, cause: Cause?): Tristate =
            if (permissions.contains(permission)) Tristate.TRUE else Tristate.UNDEFINED

    override fun permissionValue(permission: String?, contexts: MutableSet<Context>?): Tristate =
            if (permissions.contains(permission)) Tristate.TRUE else Tristate.UNDEFINED

    override fun isChildOf(parent: SubjectReference?, cause: Cause?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun isChildOf(parent: SubjectReference?, contexts: MutableSet<Context>?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun parents(cause: Cause?): MutableList<out SubjectReference> = throw NotImplementedError("MockServer player method not implemented")

    override fun parents(contexts: MutableSet<Context>?): MutableList<SubjectReference> = throw NotImplementedError("MockServer player method not implemented")

    override fun option(key: String?, cause: Cause?): Optional<String> = throw NotImplementedError("MockServer player method not implemented")

    override fun option(key: String?, contexts: MutableSet<Context>?): Optional<String> = throw NotImplementedError("MockServer player method not implemented")

    override fun user(): User = throw NotImplementedError("MockServer player method not implemented")

    override fun isOnline(): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun openInventory(): Optional<Container> = throw NotImplementedError("MockServer player method not implemented")

    override fun openInventory(inventory: Inventory?): Optional<Container> = throw NotImplementedError("MockServer player method not implemented")

    override fun openInventory(inventory: Inventory?, displayName: Component?): Optional<Container> = throw NotImplementedError("MockServer player method not implemented")

    override fun closeInventory(): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun simulateChat(message: Component?, cause: Cause?): PlayerChatEvent = throw NotImplementedError("MockServer player method not implemented")

    override fun connection(): ServerPlayerConnection = throw NotImplementedError("MockServer player method not implemented")

    override fun sendResourcePack(pack: ResourcePack?) = throw NotImplementedError("MockServer player method not implemented")

    override fun tabList(): TabList = throw NotImplementedError("MockServer player method not implemented")

    override fun kick(): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun kick(reason: Component?): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun scoreboard(): Scoreboard = throw NotImplementedError("MockServer player method not implemented")

    override fun setScoreboard(scoreboard: Scoreboard?) = throw NotImplementedError("MockServer player method not implemented")

    override fun respawn(): Boolean = throw NotImplementedError("MockServer player method not implemented")

    override fun worldBorder(): Optional<WorldBorder> = throw NotImplementedError("MockServer player method not implemented")

    override fun setWorldBorder(border: WorldBorder?) = throw NotImplementedError("MockServer player method not implemented")

    override fun cooldownTracker(): CooldownTracker = throw NotImplementedError("MockServer player method not implemented")

    override fun progress(advancement: Advancement?): AdvancementProgress = throw NotImplementedError("MockServer player method not implemented")

    override fun unlockedAdvancementTrees(): MutableCollection<AdvancementTree> = throw NotImplementedError("MockServer player method not implemented")

    override fun chatFormatter(): PlayerChatFormatter = throw NotImplementedError("MockServer player method not implemented")

    override fun setChatFormatter(router: PlayerChatFormatter?) = throw NotImplementedError("MockServer player method not implemented")
}
