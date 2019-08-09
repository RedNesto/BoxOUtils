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
package io.github.rednesto.bou.integrationTests

import com.flowpowered.math.vector.Vector3d
import io.github.rednesto.bou.BoxOUtils
import io.github.rednesto.bou.Config
import io.github.rednesto.bou.api.customdrops.CustomLoot
import io.github.rednesto.bou.api.customdrops.ItemLoot
import io.github.rednesto.bou.api.quantity.BoundedIntQuantity
import io.github.rednesto.bou.api.quantity.FixedIntQuantity
import io.github.rednesto.bou.listeners.CustomBlockDropsListener
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.spongepowered.api.Sponge
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.ExperienceOrb
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.event.item.inventory.DropItemEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.api.world.difficulty.Difficulties
import org.spongepowered.api.world.gamerule.DefaultGameRules
import org.spongepowered.mctester.api.junit.MinecraftRunner
import org.spongepowered.mctester.internal.BaseTest
import org.spongepowered.mctester.internal.event.StandaloneEventListener
import org.spongepowered.mctester.junit.TestUtils

@RunWith(MinecraftRunner::class)
class BlockDropsTests(testUtils: TestUtils) : BaseTest(testUtils) {

    lateinit var customBlockDropsListener: CustomBlockDropsListener

    @Before
    fun setUp() {
        customBlockDropsListener = CustomBlockDropsListener()
        Sponge.getEventManager().registerListeners(BoxOUtils.getInstance(), customBlockDropsListener)
    }

    @After
    fun tearDown() {
        Sponge.getEventManager().unregisterListeners(customBlockDropsListener)
        BoxOUtils.getInstance().blocksDrops = Config.BlocksDrops(false, emptyMap())
    }

    @Test
    fun `simple drop`() {
        val itemLoots = listOf(ItemLoot("minecraft:diamond_sword", null, null, 0.0, null))
        val customLoot = CustomLoot(itemLoots, null, false, false, emptyList(), null, null)
        var spawnEntityCustomCallCount = 0
        var dropItemDestructCallCount = 0
        doTest(customLoot) { event: SpawnEntityEvent ->
            if (event.isCancelled) {
                return@doTest
            }

            when (event) {
                is SpawnEntityEvent.Custom -> {
                    spawnEntityCustomCallCount++
                    val entitiesList = event.entities
                    assertTrue(entitiesList.size == 1)
                    assertTrue(entitiesList[0].getOrNull(Keys.REPRESENTED_ITEM)?.type === ItemTypes.DIAMOND_SWORD)
                }
                is DropItemEvent.Destruct -> {
                    dropItemDestructCallCount++
                    val entitiesList = event.entities
                    assertTrue(entitiesList.size == 1)
                    assertTrue(entitiesList[0].getOrNull(Keys.REPRESENTED_ITEM)?.type === ItemTypes.DIRT)
                }
            }
        }
        assertTrue("Several SpawnEntityEvent.Custom have been fired", spawnEntityCustomCallCount == 1)
        assertTrue("Several DropItemEvent.Destruct have been fired", dropItemDestructCallCount == 1)
    }

    @Test
    fun `simple overwrite drop`() {
        val itemLoots = listOf(ItemLoot("minecraft:diamond_sword", null, null, 0.0, null))
        val customLoot = CustomLoot(itemLoots, null, true, false, emptyList(), null, null)
        var spawnEntityCustomCallCount = 0
        doTest(customLoot) { event: SpawnEntityEvent ->
            if (event.isCancelled) {
                return@doTest
            }

            if (event !is SpawnEntityEvent.Custom) {
                fail("Only SpawnEntityEvent.Custom should be fired")
            }

            spawnEntityCustomCallCount++

            val entitiesList = event.entities
            assertTrue(entitiesList.size == 1)
            assertTrue(entitiesList[0].getOrNull(Keys.REPRESENTED_ITEM)?.type === ItemTypes.DIAMOND_SWORD)
        }
        assertTrue("Several SpawnEntityEvent.Custom have been fired", spawnEntityCustomCallCount == 1)
    }

    @Test
    fun `fixed experience`() {
        val customLoot = CustomLoot(emptyList(), FixedIntQuantity(5), false, false, emptyList(), null, null)
        var spawnEntityCustomCallCount = 0
        doTest(customLoot) { event: SpawnEntityEvent ->
            if (event.isCancelled) {
                return@doTest
            }

            if (event is SpawnEntityEvent.Custom) {
                spawnEntityCustomCallCount++
                val entitiesList = event.entities
                assertEquals(1, entitiesList.size)

                val entity = entitiesList[0]
                assertTrue(entity is ExperienceOrb)
                assertEquals(5, entity.getOrNull(Keys.CONTAINED_EXPERIENCE))
            }
        }
        assertTrue("Several SpawnEntityEvent.Custom have been fired", spawnEntityCustomCallCount == 1)
    }

    @Test
    fun `bounded experience`() {
        val customLoot = CustomLoot(emptyList(), BoundedIntQuantity(5, 15), false, false, emptyList(), null, null)
        var spawnEntityCustomCallCount = 0
        doTest(customLoot) { event: SpawnEntityEvent ->
            if (event.isCancelled) {
                return@doTest
            }

            if (event is SpawnEntityEvent.Custom) {
                spawnEntityCustomCallCount++
                val entitiesList = event.entities
                assertEquals(1, entitiesList.size)

                val entity = entitiesList[0]
                assertTrue(entity is ExperienceOrb)
                val containedExperience = entity.getOrNull(Keys.CONTAINED_EXPERIENCE)
                assertNotNull(containedExperience)
                assertTrue(containedExperience in 5..15)
            }
        }
        assertTrue("Several SpawnEntityEvent.Custom have been fired", spawnEntityCustomCallCount == 1)
    }

    @Test
    fun `experience overwrite`() {
        val customLoot = CustomLoot(emptyList(), null, false, true, emptyList(), null, null)
        doTest(customLoot) { event: SpawnEntityEvent ->
            if (event.isCancelled) {
                return@doTest
            }

            assertTrue(event.entities.none { it is ExperienceOrb })
        }
    }

    @Test
    fun `experience overwrite mixed`() {
        val customLoot = CustomLoot(emptyList(), FixedIntQuantity(5), false, true, emptyList(), null, null)
        doTest(customLoot) { event: SpawnEntityEvent ->
            if (event.isCancelled) {
                return@doTest
            }

            if (event is SpawnEntityEvent.Custom) {
                val entities = event.entities
                assertEquals(1, entities.size)

                val entity = entities[0]
                assertTrue(entity is ExperienceOrb)
                assertEquals(5, entity.getOrNull(Keys.CONTAINED_EXPERIENCE))
            } else {
                assertTrue(event.entities.none { it is ExperienceOrb })
            }
        }
    }

    private fun doTest(customLoot: CustomLoot, predicate: (event: SpawnEntityEvent) -> Unit) {
        BoxOUtils.getInstance().blocksDrops = Config.BlocksDrops(true, mapOf("minecraft:grass" to customLoot))

        val playerLocation: Location<World> = testUtils.thePlayer.location
        val blockLocation: Location<World> = playerLocation.add(Vector3d.FORWARD)
        testUtils.runOnMainThread(Runnable {
            blockLocation.blockType = BlockTypes.GRASS
            playerLocation.extent.properties.difficulty = Difficulties.PEACEFUL
            playerLocation.extent.properties.setGameRule(DefaultGameRules.DO_MOB_SPAWNING, "false")
            testUtils.thePlayer.offer(Keys.GAME_MODE, GameModes.SURVIVAL)
        })
        testUtils.listenTimeout(Runnable {
            client.lookAt(blockLocation.position)
            client.holdLeftClick(true)
            client.sleepTicksClient(20)
            client.holdLeftClick(false)
        }, StandaloneEventListener(SpawnEntityEvent::class.java, predicate), 25)
    }
}
