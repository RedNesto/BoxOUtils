package io.github.rednesto.bou.integrationTests

import com.flowpowered.math.vector.Vector3d
import io.github.rednesto.bou.BoxOUtils
import io.github.rednesto.bou.Config
import io.github.rednesto.bou.api.customdrops.CustomLoot
import io.github.rednesto.bou.api.customdrops.ItemLoot
import io.github.rednesto.bou.api.quantity.FixedIntQuantity
import io.github.rednesto.bou.listeners.CustomMobDropsListener
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.event.item.inventory.DropItemEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.api.world.difficulty.Difficulties
import org.spongepowered.mctester.api.junit.MinecraftRunner
import org.spongepowered.mctester.internal.BaseTest
import org.spongepowered.mctester.internal.event.StandaloneEventListener
import org.spongepowered.mctester.junit.TestUtils
import java.util.concurrent.Callable

@RunWith(MinecraftRunner::class)
class MobDropsTests(testUtils: TestUtils) : BaseTest(testUtils) {

    lateinit var customMobDropsListener: CustomMobDropsListener

    @Before
    fun setUp() {
        customMobDropsListener = CustomMobDropsListener()
        Sponge.getEventManager().registerListeners(BoxOUtils.getInstance(), customMobDropsListener)
    }

    @After
    fun tearDown() {
        Sponge.getEventManager().unregisterListeners(customMobDropsListener)
        BoxOUtils.getInstance().mobsDrops = Config.MobsDrops(false, emptyMap())
    }

    @Test
    fun `simple drop 1`() {
        val itemLoots = listOf(ItemLoot("minecraft:diamond_sword", null, null, 0.0, null))
        val customLoot = CustomLoot(itemLoots, FixedIntQuantity(0), false, false, emptyList(), null, null)
        var spawnEntityCustomCallCount = 0
        var dropItemDestructCallCount = 0
        doTest("minecraft:sheep", customLoot) { event: SpawnEntityEvent ->
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
                    // Sheeps always drop at least 1 wool
                    assertTrue(event.entities.any { it.getOrNull(Keys.REPRESENTED_ITEM)?.type === ItemTypes.WOOL })
                }
                else -> Assert.fail("Only SpawnEntityEvent.Custom or DropItemEvent.Destruct should be fired")
            }
        }
        assertTrue("Several SpawnEntityEvent.Custom have been fired", spawnEntityCustomCallCount == 1)
        assertTrue("Several DropItemEvent.Destruct have been fired", dropItemDestructCallCount == 1)
    }

    @Test
    fun `simple overwrite drop 1`() {
        val itemLoots = listOf(ItemLoot("minecraft:diamond_sword", null, null, 0.0, null))
        val customLoot = CustomLoot(itemLoots, FixedIntQuantity(0), true, false, emptyList(), null, null)
        var spawnEntityCustomCallCount = 0
        doTest("minecraft:sheep", customLoot) { event: SpawnEntityEvent ->
            if (event.isCancelled) {
                return@doTest
            }

            if (event is SpawnEntityEvent.Custom) {
                spawnEntityCustomCallCount++
            } else {
                Assert.fail("Only SpawnEntityEvent.Custom should be fired")
            }

            val entitiesList = event.entities
            assertTrue(entitiesList.size == 1)
            assertTrue(entitiesList[0].getOrNull(Keys.REPRESENTED_ITEM)?.type === ItemTypes.DIAMOND_SWORD)
        }
        assertTrue("Several SpawnEntityEvent.Custom have been fired", spawnEntityCustomCallCount == 1)
    }

    private fun doTest(blockId: String, customLoot: CustomLoot, predicate: (event: SpawnEntityEvent) -> Unit) {
        BoxOUtils.getInstance().mobsDrops = Config.MobsDrops(true, mapOf(blockId to customLoot))

        val playerLocation: Location<World> = testUtils.thePlayer.location
        val entityLocation: Location<World> = playerLocation.add(Vector3d.FORWARD)
        val entity = testUtils.runOnMainThread(Callable {
            playerLocation.extent.properties.difficulty = Difficulties.PEACEFUL
            testUtils.thePlayer.offer(Keys.GAME_MODE, GameModes.SURVIVAL)

            val entity = entityLocation.createEntity(EntityTypes.SHEEP)
            assertTrue(entity.offer(Keys.HEALTH, 1.0).isSuccessful)
            assertTrue(entityLocation.spawnEntity(entity))
            entity
        })
        testUtils.listenTimeout(Runnable {
            client.lookAt(entity)
            client.leftClick()
        }, StandaloneEventListener(SpawnEntityEvent::class.java, predicate), 5)
    }
}
