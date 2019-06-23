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
package io.github.rednesto.bou.sponge;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "box-o-utils",
        name = "Box O' Utils",
        url = "https://ore.spongepowered.org/RedNesto/Box-O'-Utils",
        description = "Control what blocks/mobs can loot (items, experience, money), right-click to harvest; more to come",
        authors = {"RedNesto"},
        dependencies = {
                @Dependency(id = "file-inventories", version = "[0.3.0,)", optional = true),
                @Dependency(id = "byte-items", version = "[2.3,)", optional = true)
        }
)
public class BoxOUtils {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private static BoxOUtils instance;

    @Listener
    public void onConstruction(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent event) {
        this.logger.info("Loading built-in integrations");
        IntegrationsManager.INSTANCE.loadBuiltins();
        IntegrationsManager.INSTANCE.initIntegrations(this);

        try {
            SpongeConfig.loadConf(this);
        } catch (IOException e) {
            this.logger.error("An exception occurred when loading configuration", e);
        }
    }

    @Listener
    public void onConfigReload(GameReloadEvent event) {
        try {
            SpongeConfig.loadConf(this);
        } catch (IOException e) {
            this.logger.error("An exception occurred when reloading configuration", e);
            event.getCause().first(CommandSource.class)
                    .filter(source -> !(source instanceof ConsoleSource))
                    .ifPresent(source -> source.sendMessage(Text.of(TextColors.RED, "[Box O' Utils] Unable to reload configuration: " + e.getMessage())));
        }
    }

    public static BoxOUtils getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getConfigDir() {
        return configDir;
    }
}
