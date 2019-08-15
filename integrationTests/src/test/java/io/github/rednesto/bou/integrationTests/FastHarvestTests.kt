package io.github.rednesto.bou.integrationTests

import com.flowpowered.math.vector.Vector3d
import io.github.rednesto.bou.BoxOUtils
import io.github.rednesto.bou.Config
import io.github.rednesto.bou.api.fastharvest.FastHarvestCrop
import io.github.rednesto.bou.api.fastharvest.FastHarvestTools
import io.github.rednesto.bou.listeners.FastHarvestListener
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.spongepowered.api.Sponge
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.event.entity.SpawnEntityEvent
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
class FastHarvestTests(testUtils: TestUtils) : BaseTest(testUtils) {

    lateinit var fastHarvestListener: FastHarvestListener

    @Before
    fun setUp() {
        fastHarvestListener = FastHarvestListener()
        Sponge.getEventManager().registerListeners(BoxOUtils.getInstance(), fastHarvestListener)
    }

    @After
    fun tearDown() {
        Sponge.getEventManager().unregisterListeners(fastHarvestListener)
        BoxOUtils.getInstance().fastHarvest = createHarvestConf(enabled = false)
    }

    @Test
    fun `carrot minimum drop`() {
        val harvestCrop = FastHarvestCrop(-1, -1, 0, 1, 5)
        doTest(harvestCrop) { event: SpawnEntityEvent ->
            if (event.isCancelled) {
                return@doTest
            }

            if (event !is SpawnEntityEvent.Custom) {
                println(event)
                fail()
            }

            val entities = event.entities
            assertEquals(1, entities.size)

            val entity = entities[0]
            val representedItem = entity.getOrNull(Keys.REPRESENTED_ITEM)
            if (representedItem == null) {
                fail()
                return@doTest
            }

            assertEquals(ItemTypes.CARROT, representedItem.type)
            assertEquals(4, representedItem.quantity)
        }
    }

    @Test
    fun `immature crop harvest`() {
        val harvestCrop = FastHarvestCrop(-1, -1, 0, 1, 5)
        try {
            doTest(harvestCrop, false) { event: SpawnEntityEvent ->
                if (event.isCancelled) {
                    return@doTest
                }

                fail("Immature crop should not be harvested")
            }
        } catch (e: java.lang.AssertionError) {
            // Exceptions starting with this are expected since we do not want a SpawnEntityEvent to be fired.
            // And yes, the typo is in the original message.
            if (e.message?.startsWith("A timed listener failed to run in the alloted number of ticks!") == false) {
                throw e
            }
        }
    }

    private fun doTest(harvestCrop: FastHarvestCrop, matureCrop: Boolean = true, predicate: (event: SpawnEntityEvent) -> Unit) {
        BoxOUtils.getInstance().fastHarvest = createHarvestConf(enabled = true, carrot = harvestCrop)

        val playerLocation: Location<World> = testUtils.thePlayer.location
        val cropLocation: Location<World> = playerLocation.add(Vector3d.FORWARD)
        val soilLocation: Location<World> = playerLocation.add(0.0, -1.0, 1.0)
        val waterLocation: Location<World> = playerLocation.add(0.0, -1.0, -1.0)
        testUtils.runOnMainThread(Runnable {
            playerLocation.extent.properties.difficulty = Difficulties.PEACEFUL
            playerLocation.extent.properties.setGameRule(DefaultGameRules.DO_MOB_SPAWNING, "false")
            testUtils.thePlayer.offer(Keys.GAME_MODE, GameModes.SURVIVAL)

            cropLocation.blockType = BlockTypes.CARROTS
            if (matureCrop) {
                cropLocation.block = cropLocation.block.with(Keys.GROWTH_STAGE, 7).get()
            }
            soilLocation.blockType = BlockTypes.FARMLAND
            waterLocation.blockType = BlockTypes.WATER
        })
        testUtils.listenTimeout(Runnable {
            client.lookAt(cropLocation.position)
            client.rightClick()
        }, StandaloneEventListener(SpawnEntityEvent::class.java, predicate), 5)
    }

    private fun createHarvestConf(enabled: Boolean = true,
                                  tools: FastHarvestTools = FastHarvestTools(false, true, listOf()),
                                  beetroot: FastHarvestCrop = FastHarvestCrop.createDefault(),
                                  beetrootSeed: FastHarvestCrop = FastHarvestCrop.createDefault(),
                                  carrot: FastHarvestCrop = FastHarvestCrop.createDefault(),
                                  potato: FastHarvestCrop = FastHarvestCrop.createDefault(),
                                  seed: FastHarvestCrop = FastHarvestCrop.createDefault(),
                                  wheat: FastHarvestCrop = FastHarvestCrop.createDefault()): Config.FastHarvest {
        return Config.FastHarvest(enabled, beetroot, beetrootSeed, carrot, potato, seed, wheat, tools)
    }
}
