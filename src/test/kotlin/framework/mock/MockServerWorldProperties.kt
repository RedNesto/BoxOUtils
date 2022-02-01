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

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.KeyedValue
import net.kyori.adventure.text.Component
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.trader.WanderingTrader
import org.spongepowered.api.util.MinecraftDayTime
import org.spongepowered.api.util.Ticks
import org.spongepowered.api.world.SerializationBehavior
import org.spongepowered.api.world.WorldType
import org.spongepowered.api.world.border.WorldBorder
import org.spongepowered.api.world.difficulty.Difficulty
import org.spongepowered.api.world.gamerule.GameRule
import org.spongepowered.api.world.generation.config.WorldGenerationConfig
import org.spongepowered.api.world.server.ServerWorld
import org.spongepowered.api.world.server.storage.ServerWorldProperties
import org.spongepowered.api.world.weather.Weather
import org.spongepowered.api.world.weather.WeatherType
import org.spongepowered.math.vector.Vector3i
import java.util.*

class MockServerWorldProperties(
        private val uuid: UUID = UUID.randomUUID(),
        private val displayname: Component? = Component.text("test")
) : ServerWorldProperties {

    constructor(uuid: UUID, displayname: String) : this(uuid, Component.text(displayname))

    override fun weather(): Weather = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun <V : Any?> gameRule(gameRule: GameRule<V>?): V = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun <V : Any?> setGameRule(gameRule: GameRule<V>?, value: V) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun gameRules(): MutableMap<GameRule<*>, *> = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun spawnPosition(): Vector3i = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setSpawnPosition(position: Vector3i?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun gameTime(): MinecraftDayTime = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun dayTime(): MinecraftDayTime = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun hardcore(): Boolean = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun difficulty(): Difficulty = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun worldBorder(): WorldBorder = throw NotImplementedError("MockServerWorld method not implemented")

    override fun uniqueId(): UUID = uuid

    override fun setWeather(weather: WeatherType?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setWeather(weather: WeatherType?, ticks: Ticks?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun key(): ResourceKey = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun world(): Optional<ServerWorld> = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun displayName(): Optional<Component> = Optional.ofNullable(displayname)

    override fun setDisplayName(name: Component?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun initialized(): Boolean = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun loadOnStartup(): Boolean = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setLoadOnStartup(loadOnStartup: Boolean) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun performsSpawnLogic(): Boolean = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setPerformsSpawnLogic(performsSpawnLogic: Boolean) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun worldGenerationConfig(): WorldGenerationConfig.Mutable = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setDayTime(time: MinecraftDayTime?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun worldType(): WorldType = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setWorldType(worldType: WorldType?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun pvp(): Boolean = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setPvp(pvp: Boolean) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun gameMode(): GameMode = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setGameMode(gameMode: GameMode?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setHardcore(hardcore: Boolean) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun commands(): Boolean = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setCommands(commands: Boolean) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setDifficulty(difficulty: Difficulty?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun serializationBehavior(): SerializationBehavior = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setSerializationBehavior(behavior: SerializationBehavior?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun wanderingTraderSpawnDelay(): Ticks = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setWanderingTraderSpawnDelay(delay: Ticks?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun wanderingTraderSpawnChance(): Int = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setWanderingTraderSpawnChance(chance: Int) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun wanderTraderUniqueId(): Optional<UUID> = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setWanderingTrader(trader: WanderingTrader?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun customBossBars(): MutableList<KeyedValue<BossBar>> = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setCustomBossBars(bars: MutableList<KeyedValue<BossBar>>?) = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun viewDistance(): Int = throw NotImplementedError("MockServerWorldProperties method not implemented")

    override fun setViewDistance(viewDistance: Int?) = throw NotImplementedError("MockServerWorldProperties method not implemented")
}
