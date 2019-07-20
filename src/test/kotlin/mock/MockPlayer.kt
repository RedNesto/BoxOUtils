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
package io.github.rednesto.bou.tests.mock

import com.flowpowered.math.vector.Vector3d
import com.flowpowered.math.vector.Vector3i
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementProgress
import org.spongepowered.api.advancement.AdvancementTree
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.data.*
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.manipulator.DataManipulator
import org.spongepowered.api.data.merge.MergeFunction
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.SkinPart
import org.spongepowered.api.data.value.BaseValue
import org.spongepowered.api.data.value.immutable.ImmutableValue
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.sound.SoundCategory
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.effect.sound.record.RecordType
import org.spongepowered.api.entity.*
import org.spongepowered.api.entity.living.player.CooldownTracker
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.tab.TabList
import org.spongepowered.api.entity.projectile.Projectile
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.message.MessageChannelEvent
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.Container
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.item.inventory.type.CarriedInventory
import org.spongepowered.api.network.PlayerConnection
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.resourcepack.ResourcePack
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.text.BookView
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.chat.ChatType
import org.spongepowered.api.text.chat.ChatVisibility
import org.spongepowered.api.text.title.Title
import org.spongepowered.api.text.translation.Translation
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.RelativePositions
import org.spongepowered.api.util.Tristate
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.api.world.WorldBorder
import java.util.*

class MockPlayer(private val permissions: List<String> = listOf(), private val location: Location<World> = Location(MockWorld(), Vector3d.ZERO)) : Player {
    override fun getTranslation(): Translation = throw NotImplementedError("MockPlayer method not implemented")

    override fun getContentVersion(): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun getConnection(): PlayerConnection = throw NotImplementedError("MockPlayer method not implemented")

    override fun getProgress(advancement: Advancement): AdvancementProgress = throw NotImplementedError("MockPlayer method not implemented")

    override fun isOnGround(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun <E : Any?> offer(key: Key<out BaseValue<E>>, value: E): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun offer(valueContainer: DataManipulator<*, *>, function: MergeFunction): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun openInventory(inventory: Inventory): Optional<Container> = throw NotImplementedError("MockPlayer method not implemented")

    override fun openInventory(inventory: Inventory, displayName: Text): Optional<Container> = throw NotImplementedError("MockPlayer method not implemented")

    override fun transferToWorld(world: World, position: Vector3d): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getApplicableProperties(): MutableCollection<Property<*, *>> = throw NotImplementedError("MockPlayer method not implemented")

    override fun equip(type: EquipmentType, equipment: ItemStack?): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getScale(): Vector3d = throw NotImplementedError("MockPlayer method not implemented")

    override fun getWorldUniqueId(): Optional<UUID> = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendResourcePack(pack: ResourcePack) = throw NotImplementedError("MockPlayer method not implemented")

    override fun createArchetype(): EntityArchetype = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendMessage(message: Text) = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendMessage(type: ChatType, message: Text) = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendBlockChange(x: Int, y: Int, z: Int, state: BlockState) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getVehicle(): Optional<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getIdentifier(): String = throw NotImplementedError("MockPlayer method not implemented")

    override fun setSleepingIgnored(sleepingIgnored: Boolean) = throw NotImplementedError("MockPlayer method not implemented")

    override fun canEquip(type: EquipmentType): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun canEquip(type: EquipmentType, equipment: ItemStack?): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getLocation(): Location<World> = location

    override fun getMessageChannel(): MessageChannel = throw NotImplementedError("MockPlayer method not implemented")

    override fun createSnapshot(): EntitySnapshot = throw NotImplementedError("MockPlayer method not implemented")

    override fun supports(holderClass: Class<out DataManipulator<*, *>>): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun supports(key: Key<*>): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun isSleepingIgnored(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getEnderChestInventory(): Inventory = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBoundingBox(): Optional<AABB> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getSubjectData(): SubjectData = throw NotImplementedError("MockPlayer method not implemented")

    override fun isChildOf(contexts: MutableSet<Context>, parent: SubjectReference): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun undo(result: DataTransactionResult): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun getBaseVehicle(): Entity = throw NotImplementedError("MockPlayer method not implemented")

    override fun playSound(sound: SoundType, category: SoundCategory, position: Vector3d, volume: Double) = throw NotImplementedError("MockPlayer method not implemented")

    override fun playSound(sound: SoundType, category: SoundCategory, position: Vector3d, volume: Double, pitch: Double) = throw NotImplementedError("MockPlayer method not implemented")

    override fun playSound(sound: SoundType, category: SoundCategory, position: Vector3d, volume: Double, pitch: Double, minVolume: Double) = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopSounds() = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopSounds(sound: SoundType) = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopSounds(category: SoundCategory) = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopSounds(sound: SoundType, category: SoundCategory) = throw NotImplementedError("MockPlayer method not implemented")

    override fun isChatColorsEnabled(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun asSubjectReference(): SubjectReference = throw NotImplementedError("MockPlayer method not implemented")

    override fun <T : Projectile?> launchProjectile(projectileClass: Class<T>): Optional<T> = throw NotImplementedError("MockPlayer method not implemented")

    override fun <T : Projectile?> launchProjectile(projectileClass: Class<T>, velocity: Vector3d): Optional<T> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getTransform(): Transform<World> = throw NotImplementedError("MockPlayer method not implemented")

    override fun setItemInHand(hand: HandType, itemInHand: ItemStack?) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getTransientSubjectData(): SubjectData = throw NotImplementedError("MockPlayer method not implemented")

    override fun setRotation(rotation: Vector3d) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getCooldownTracker(): CooldownTracker = throw NotImplementedError("MockPlayer method not implemented")

    override fun addPassenger(entity: Entity): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun setSpectatorTarget(entity: Entity?) = throw NotImplementedError("MockPlayer method not implemented")

    override fun <T : Property<*, *>?> getProperty(propertyClass: Class<T>): Optional<T> = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendTitle(title: Title) = throw NotImplementedError("MockPlayer method not implemented")

    override fun toContainer(): DataContainer = throw NotImplementedError("MockPlayer method not implemented")

    override fun setWorldBorder(border: WorldBorder?, cause: Cause) = throw NotImplementedError("MockPlayer method not implemented")

    override fun <E : Any?, V : BaseValue<E>?> getValue(key: Key<V>): Optional<V> = throw NotImplementedError("MockPlayer method not implemented")

    override fun <T : DataManipulator<*, *>?> get(containerClass: Class<T>): Optional<T> = throw NotImplementedError("MockPlayer method not implemented")

    override fun <E : Any?> get(key: Key<out BaseValue<E>>): Optional<E> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getActiveContexts(): MutableSet<Context> = mutableSetOf()

    override fun setTransform(transform: Transform<World>): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getSpectatorTarget(): Optional<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getCommandSource(): Optional<CommandSource> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getKeys(): MutableSet<Key<*>> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getUnlockedAdvancementTrees(): MutableCollection<AdvancementTree> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getType(): EntityType = throw NotImplementedError("MockPlayer method not implemented")

    override fun getParents(contexts: MutableSet<Context>): MutableList<SubjectReference> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getPlayer(): Optional<Player> = throw NotImplementedError("MockPlayer method not implemented")

    override fun resetBlockChange(x: Int, y: Int, z: Int) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getPosition(): Vector3d = throw NotImplementedError("MockPlayer method not implemented")

    override fun sendBookView(bookView: BookView) = throw NotImplementedError("MockPlayer method not implemented")

    override fun kick() = throw NotImplementedError("MockPlayer method not implemented")

    override fun kick(reason: Text) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getRandom(): Random = throw NotImplementedError("MockPlayer method not implemented")

    override fun getValues(): MutableSet<ImmutableValue<*>> = throw NotImplementedError("MockPlayer method not implemented")

    override fun respawnPlayer(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun setVehicle(entity: Entity?): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getChatVisibility(): ChatVisibility = throw NotImplementedError("MockPlayer method not implemented")

    override fun getRotation(): Vector3d = throw NotImplementedError("MockPlayer method not implemented")

    override fun copyFrom(that: DataHolder, function: MergeFunction): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun setScoreboard(scoreboard: Scoreboard) = throw NotImplementedError("MockPlayer method not implemented")

    override fun setMessageChannel(channel: MessageChannel) = throw NotImplementedError("MockPlayer method not implemented")

    override fun simulateChat(message: Text, cause: Cause): MessageChannelEvent.Chat = throw NotImplementedError("MockPlayer method not implemented")

    override fun getTeamRepresentation(): Text = throw NotImplementedError("MockPlayer method not implemented")

    override fun copy(): DataHolder = throw NotImplementedError("MockPlayer method not implemented")

    override fun <T : DataManipulator<*, *>?> getOrCreate(containerClass: Class<T>): Optional<T> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getHeadRotation(): Vector3d = throw NotImplementedError("MockPlayer method not implemented")

    override fun getCreator(): Optional<UUID> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getContainingCollection(): SubjectCollection = throw NotImplementedError("MockPlayer method not implemented")

    override fun isLoaded(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getPermissionValue(contexts: MutableSet<Context>, permission: String): Tristate = Tristate.fromBoolean(permissions.contains(permission))

    override fun isOnline(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun stopRecord(position: Vector3i) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getNotifier(): Optional<UUID> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getViewDistance(): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun setLocationAndRotation(location: Location<World>, rotation: Vector3d): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun setLocationAndRotation(location: Location<World>, rotation: Vector3d, relativePositions: EnumSet<RelativePositions>): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun removePassenger(entity: Entity) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getOption(contexts: MutableSet<Context>, key: String): Optional<String> = throw NotImplementedError("MockPlayer method not implemented")

    override fun getUniqueId(): UUID = throw NotImplementedError("MockPlayer method not implemented")

    override fun getInventory(): CarriedInventory<out Carrier> = throw NotImplementedError("MockPlayer method not implemented")

    override fun clearPassengers() = throw NotImplementedError("MockPlayer method not implemented")

    override fun getWorldBorder(): Optional<WorldBorder> = throw NotImplementedError("MockPlayer method not implemented")

    override fun spawnParticles(particleEffect: ParticleEffect, position: Vector3d) = throw NotImplementedError("MockPlayer method not implemented")

    override fun spawnParticles(particleEffect: ParticleEffect, position: Vector3d, radius: Int) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getOpenInventory(): Optional<Container> = throw NotImplementedError("MockPlayer method not implemented")

    override fun hasPassenger(entity: Entity): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getTabList(): TabList = throw NotImplementedError("MockPlayer method not implemented")

    override fun closeInventory(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun setRawData(container: DataView) = throw NotImplementedError("MockPlayer method not implemented")

    override fun setNotifier(uuid: UUID?) = throw NotImplementedError("MockPlayer method not implemented")

    override fun setCreator(uuid: UUID?) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getName(): String = throw NotImplementedError("MockPlayer method not implemented")

    override fun getItemInHand(handType: HandType): Optional<ItemStack> = throw NotImplementedError("MockPlayer method not implemented")

    override fun setLocation(location: Location<World>): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun setLocation(position: Vector3d, world: UUID): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun isRemoved(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getContainers(): MutableCollection<DataManipulator<*, *>> = throw NotImplementedError("MockPlayer method not implemented")

    override fun setScale(scale: Vector3d) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getPassengers(): MutableList<Entity> = throw NotImplementedError("MockPlayer method not implemented")

    override fun damage(damage: Double, damageSource: DamageSource): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun setHeadRotation(rotation: Vector3d) = throw NotImplementedError("MockPlayer method not implemented")

    override fun getEquipped(type: EquipmentType): Optional<ItemStack> = throw NotImplementedError("MockPlayer method not implemented")

    override fun playRecord(position: Vector3i, recordType: RecordType) = throw NotImplementedError("MockPlayer method not implemented")

    override fun validateRawData(container: DataView): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getDisplayedSkinParts(): MutableSet<SkinPart> = throw NotImplementedError("MockPlayer method not implemented")

    override fun remove() = throw NotImplementedError("MockPlayer method not implemented")

    override fun remove(containerClass: Class<out DataManipulator<*, *>>): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun remove(key: Key<*>): DataTransactionResult = throw NotImplementedError("MockPlayer method not implemented")

    override fun getProfile(): GameProfile = throw NotImplementedError("MockPlayer method not implemented")

    override fun getScoreboard(): Scoreboard = throw NotImplementedError("MockPlayer method not implemented")

    override fun isSubjectDataPersisted(): Boolean = throw NotImplementedError("MockPlayer method not implemented")
}
