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
package io.github.rednesto.bou.tests.io.github.rednesto.bou.tests.framework.mock

import io.github.rednesto.bou.tests.framework.mock.MockRegistryHolder
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.spongepowered.api.Game
import org.spongepowered.api.Server
import org.spongepowered.api.command.manager.CommandManager
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.CauseStackManager
import org.spongepowered.api.item.recipe.RecipeManager
import org.spongepowered.api.map.MapStorage
import org.spongepowered.api.profile.GameProfileManager
import org.spongepowered.api.registry.RegistryHolder
import org.spongepowered.api.resource.ResourceManager
import org.spongepowered.api.resource.pack.PackRepository
import org.spongepowered.api.resourcepack.ResourcePack
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.service.ServiceProvider
import org.spongepowered.api.user.UserManager
import org.spongepowered.api.util.Ticks
import org.spongepowered.api.world.difficulty.Difficulty
import org.spongepowered.api.world.generation.config.WorldGenerationConfig
import org.spongepowered.api.world.server.WorldManager
import org.spongepowered.api.world.storage.ChunkLayout
import org.spongepowered.api.world.teleport.TeleportHelper
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.CompletableFuture

class MockServer : Server, RegistryHolder by MockRegistryHolder() {

    override fun audiences(): MutableIterable<Audience> = throw NotImplementedError("MockServer method not implemented")

    override fun game(): Game = throw NotImplementedError("MockServer method not implemented")

    override fun causeStackManager(): CauseStackManager = throw NotImplementedError("MockServer method not implemented")

    override fun packRepository(): PackRepository = throw NotImplementedError("MockServer method not implemented")

    override fun resourceManager(): ResourceManager = throw NotImplementedError("MockServer method not implemented")

    override fun scheduler(): Scheduler = throw NotImplementedError("MockServer method not implemented")

    override fun onMainThread(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun reloadResources(): CompletableFuture<Void> = throw NotImplementedError("MockServer method not implemented")

    override fun worldManager(): WorldManager = throw NotImplementedError("MockServer method not implemented")

    override fun recipeManager(): RecipeManager = throw NotImplementedError("MockServer method not implemented")

    override fun isMultiWorldEnabled(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun worldGenerationConfig(): WorldGenerationConfig = throw NotImplementedError("MockServer method not implemented")

    override fun maxPlayers(): Int = throw NotImplementedError("MockServer method not implemented")

    override fun isWhitelistEnabled(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun isOnlineModeEnabled(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun motd(): Component = throw NotImplementedError("MockServer method not implemented")

    override fun resourcePack(): Optional<ResourcePack> = throw NotImplementedError("MockServer method not implemented")

    override fun playerIdleTimeout(): Int = throw NotImplementedError("MockServer method not implemented")

    override fun isHardcoreModeEnabled(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun difficulty(): Difficulty = throw NotImplementedError("MockServer method not implemented")

    override fun gameMode(): GameMode = throw NotImplementedError("MockServer method not implemented")

    override fun isGameModeEnforced(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun isPVPEnabled(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun areCommandBlocksEnabled(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun isMonsterSpawnsEnabled(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun isAnimalSpawnsEnabled(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun isDedicatedServer(): Boolean = throw NotImplementedError("MockServer method not implemented")

    override fun userManager(): UserManager = throw NotImplementedError("MockServer method not implemented")

    override fun teleportHelper(): TeleportHelper = throw NotImplementedError("MockServer method not implemented")

    override fun onlinePlayers(): MutableCollection<ServerPlayer> = throw NotImplementedError("MockServer method not implemented")

    override fun player(uniqueId: UUID?): Optional<ServerPlayer> = throw NotImplementedError("MockServer method not implemented")

    override fun player(name: String?): Optional<ServerPlayer> = throw NotImplementedError("MockServer method not implemented")

    override fun serverScoreboard(): Optional<out Scoreboard> = throw NotImplementedError("MockServer method not implemented")

    override fun chunkLayout(): ChunkLayout = throw NotImplementedError("MockServer method not implemented")

    override fun runningTimeTicks(): Ticks = throw NotImplementedError("MockServer method not implemented")

    override fun broadcastAudience(): Audience = throw NotImplementedError("MockServer method not implemented")

    override fun setBroadcastAudience(channel: Audience?) = throw NotImplementedError("MockServer method not implemented")

    override fun boundAddress(): Optional<InetSocketAddress> = throw NotImplementedError("MockServer method not implemented")

    override fun setHasWhitelist(enabled: Boolean) = throw NotImplementedError("MockServer method not implemented")

    override fun shutdown() = throw NotImplementedError("MockServer method not implemented")

    override fun shutdown(kickMessage: Component?) = throw NotImplementedError("MockServer method not implemented")

    override fun gameProfileManager(): GameProfileManager = throw NotImplementedError("MockServer method not implemented")

    override fun ticksPerSecond(): Double = throw NotImplementedError("MockServer method not implemented")

    override fun averageTickTime(): Double = throw NotImplementedError("MockServer method not implemented")

    override fun targetTicksPerSecond(): Int = throw NotImplementedError("MockServer method not implemented")

    override fun setPlayerIdleTimeout(timeout: Int) = throw NotImplementedError("MockServer method not implemented")

    override fun serviceProvider(): ServiceProvider.ServerScoped = throw NotImplementedError("MockServer method not implemented")

    override fun commandManager(): CommandManager = throw NotImplementedError("MockServer method not implemented")

    override fun mapStorage(): MapStorage = throw NotImplementedError("MockServer method not implemented")
}
