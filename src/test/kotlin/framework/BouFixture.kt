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
package io.github.rednesto.bou.tests.framework

import io.github.rednesto.bou.BouUtils
import io.github.rednesto.bou.BoxOUtils
import io.github.rednesto.bou.IntegrationsManager
import io.github.rednesto.bou.tests.framework.mock.MockGame
import io.github.rednesto.bou.tests.framework.mock.MockPluginContainer
import org.spongepowered.api.Sponge
import java.nio.file.Path

class BouFixture(private val configDirProvider: () -> Path, val loadBuiltinIntegrations: Boolean = true) {

    lateinit var plugin: BoxOUtils
        private set

    fun setUp() {
        with(Sponge::class.java.getDeclaredField("game")) {
            isAccessible = true
            set(null, MockGame())
            isAccessible = false
        }
        plugin = BoxOUtils(MockPluginContainer(), configDirProvider(), IntegrationsManager())
        BoxOUtils.setInstance(plugin)
        if (loadBuiltinIntegrations) {
            val integrationsManager = plugin.integrationsManager
            integrationsManager.loadVanillaBuiltins()
            BouUtils.registerIntegrations(integrationsManager, true)
        }
    }
}
