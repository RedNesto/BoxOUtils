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

import io.github.rednesto.bou.tests.io.github.rednesto.bou.tests.framework.mock.MockServer
import org.spongepowered.api.Game
import org.spongepowered.api.Platform
import org.spongepowered.api.Server
import org.spongepowered.api.SystemSubject
import org.spongepowered.api.config.ConfigManager
import org.spongepowered.api.data.DataManager
import org.spongepowered.api.event.EventManager
import org.spongepowered.api.network.channel.ChannelManager
import org.spongepowered.api.plugin.PluginManager
import org.spongepowered.api.registry.BuilderProvider
import org.spongepowered.api.registry.FactoryProvider
import org.spongepowered.api.registry.RegistryHolder
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.service.ServiceProvider
import org.spongepowered.api.sql.SqlManager
import org.spongepowered.api.util.metric.MetricsConfigManager
import java.nio.file.Path
import java.util.*

class MockGame(
        val myFactoryProvider: FactoryProvider = MockFactoryProvider(),
        registryHolder: RegistryHolder = MockRegistryHolder()
) : Game, RegistryHolder by registryHolder {

    override fun asyncScheduler(): Scheduler = throw NotImplementedError("MockGame method not implemented")

    override fun gameDirectory(): Path = throw NotImplementedError("MockGame method not implemented")

    override fun isServerAvailable(): Boolean = throw NotImplementedError("MockGame method not implemented")

    override fun server(): Server = MockServer()

    override fun systemSubject(): SystemSubject = throw NotImplementedError("MockGame method not implemented")

    override fun locale(locale: String): Locale = throw NotImplementedError("MockGame method not implemented")

    override fun platform(): Platform = MockPlatform

    override fun builderProvider(): BuilderProvider = throw NotImplementedError("MockGame method not implemented")

    override fun factoryProvider(): FactoryProvider = myFactoryProvider

    override fun dataManager(): DataManager = throw NotImplementedError("MockGame method not implemented")

    override fun pluginManager(): PluginManager = throw NotImplementedError("MockGame method not implemented")

    override fun eventManager(): EventManager = throw NotImplementedError("MockGame method not implemented")

    override fun configManager(): ConfigManager = throw NotImplementedError("MockGame method not implemented")

    override fun channelManager(): ChannelManager = throw NotImplementedError("MockGame method not implemented")

    override fun metricsConfigManager(): MetricsConfigManager = throw NotImplementedError("MockGame method not implemented")

    override fun sqlManager(): SqlManager = throw NotImplementedError("MockGame method not implemented")

    override fun serviceProvider(): ServiceProvider.GameScoped = throw NotImplementedError("MockGame method not implemented")
}
