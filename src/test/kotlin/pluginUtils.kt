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

import io.github.rednesto.bou.api.customdrops.*
import io.github.rednesto.bou.api.lootReuse.LootReuse
import io.github.rednesto.bou.api.quantity.IntQuantity
import io.github.rednesto.bou.api.requirement.Requirement
import io.github.rednesto.bou.integration.byteitems.ByteItemsCustomDropsProvider
import io.github.rednesto.bou.integration.customdrops.recipients.ContextLocationLootRecipient
import io.github.rednesto.bou.integration.fileinventories.FileInventoriesCustomDropsProvider
import io.github.rednesto.bou.integration.vanilla.VanillaCustomDropsProvider
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

fun customLoot(
        dropsProviders: List<CustomDropsProvider> = emptyList(),
        overwrite: Boolean = false,
        expOverwrite: Boolean = false,
        chance: Double = 0.0,
        recipient: CustomLootRecipient = ContextLocationLootRecipient.INSTANCE,
        redirectBaseDropsToRecipient: Boolean = true,
        requirements: List<List<Requirement>> = emptyList(),
        reuse: CustomLoot.Reuse? = null,
        components: List<CustomLootComponent> = emptyList()
) = CustomLoot(dropsProviders, overwrite, expOverwrite, chance, recipient, redirectBaseDropsToRecipient, requirements, reuse, components)

fun customReuse(
        multiplier: Float = 1f,
        items: Map<String, LootReuse> = emptyMap(),
        requirements: List<List<Requirement>> = emptyList()
) = CustomLoot.Reuse(multiplier, items, requirements)

fun vanillaDrop(
        id: String,
        displayname: String? = null,
        chance: Double = 0.0,
        quantity: IntQuantity? = null,
        unsafeDamage: Int? = null
) = VanillaCustomDropsProvider(id, displayname, chance, quantity, unsafeDamage)

fun byteItemsDrop(
        id: String,
        displayname: String? = null,
        chance: Double = 0.0,
        quantity: IntQuantity? = null
) = ByteItemsCustomDropsProvider(id, displayname, chance, quantity)

fun fileInvDrop(
        id: String,
        displayname: String? = null,
        chance: Double = 0.0,
        quantity: IntQuantity? = null
) = FileInventoriesCustomDropsProvider(id, displayname, chance, quantity)

fun lootProcessingContext(
        loots: List<CustomLoot>,
        source: Any,
        cause: Cause,
        event: Event? = null,
        targetPlayer: Player? = null,
        targetLocation: Location<World>? = null,
        experienceSpawnLocation: Location<World>? = null
) = CustomLootProcessingContext(loots, event, source, cause, targetPlayer, targetLocation, experienceSpawnLocation)
