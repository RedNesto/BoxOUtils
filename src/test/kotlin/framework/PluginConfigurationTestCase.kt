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

import com.google.common.reflect.TypeToken
import io.github.rednesto.bou.SpongeConfig
import org.junit.jupiter.api.fail
import java.nio.file.Path
import java.nio.file.Paths

open class PluginConfigurationTestCase<T>(val configDir: String, val typeToken: TypeToken<T>) : BouTestCase(noSponge = true) {

    protected fun loadConfig(testName: String): T {
        val configFile = plugin.configDir.resolve("$testName.conf")
        val node = SpongeConfig.loader(configFile).load()
        return node.getValue(typeToken) ?: fail("Configuration value is null")
    }

    override fun createConfigDir(): Path {
        val folderUri = javaClass.getResource("/configurationTests/$configDir") ?: fail { "config folder does not exist" }
        return Paths.get(folderUri.toURI())
    }
}
