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
package io.github.rednesto.bou;

import com.google.inject.Inject;
import io.github.rednesto.bou.commands.BouInspectItemCommand;
import io.github.rednesto.bou.commands.BouReloadCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

@Plugin("box-o-utils")
public class BoxOUtils {

    private final PluginContainer container;
    private final Logger logger;

    private final Path configDir;
    private final IntegrationsManager integrationsManager;

    private Config.BlocksDrops blocksDrops = new Config.BlocksDrops(false, new HashMap<>());
    private Config.MobsDrops mobsDrops = new Config.MobsDrops(false, new HashMap<>());
    private Config.FishingDrops fishingDrops = new Config.FishingDrops(false, new ArrayList<>());
    private Config.BlockSpawners blockSpawners = new Config.BlockSpawners(false, new HashMap<>());
    private Config.FastHarvest fastHarvest = Config.FastHarvest.createDefault();
    private Config.CropsControl cropsControl = Config.CropsControl.createDefault();

    private static @MonotonicNonNull BoxOUtils instance;

    @Inject
    public BoxOUtils(PluginContainer container, @ConfigDir(sharedRoot = false) Path configDir) {
        this(container, configDir, new IntegrationsManager());
    }

    public BoxOUtils(PluginContainer container, Path configDir, IntegrationsManager integrationsManager) {
        this.container = container;
        this.logger = container.logger();
        this.configDir = configDir;
        this.integrationsManager = integrationsManager;
    }

    @Listener
    public void onConstruct(ConstructPluginEvent event) {
        instance = this;
    }

    @Listener
    public void onPreInit(StartingEngineEvent<Server> event) {
        this.logger.info("Loading built-in integrations");
        integrationsManager.loadVanillaBuiltins();
        BouUtils.registerIntegrations(integrationsManager, false);

        try {
            SpongeConfig.loadConf(this);
        } catch (IOException e) {
            this.logger.error("An exception occurred when loading configuration", e);
        }
        integrationsManager.initIntegrations(this);
    }

    @Listener
    public void onGameInitialization(RegisterCommandEvent<Command.Parameterized> event) {
        Command.Parameterized bou = Command.builder()
                .addChild(BouReloadCommand.create(), "reload")
                .addChild(Command.builder().addChild(BouInspectItemCommand.create(), "item").build(), "inspect")
                .build();
        event.register(this.container, bou, "boxoutils", "bou");
    }

    @Listener
    public void onConfigReload(RefreshGameEvent event) {
        try {
            SpongeConfig.loadConf(this);
            integrationsManager.reloadIntegrations(this);
        } catch (IOException e) {
            this.logger.error("An exception occurred when reloading configuration", e);
            event.cause().first(Audience.class)
                    .filter(source -> !(source instanceof Server))
                    .ifPresent(source -> source.sendMessage(Component.text("[Box O' Utils] Unable to reload configuration: " + e.getMessage(), NamedTextColor.RED)));
        }
    }

    public PluginContainer getContainer() {
        return container;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getConfigDir() {
        return configDir;
    }

    public IntegrationsManager getIntegrationsManager() {
        return integrationsManager;
    }

    public Config.BlocksDrops getBlocksDrops() {
        return blocksDrops;
    }

    public void setBlocksDrops(Config.BlocksDrops blocksDrops) {
        this.blocksDrops = blocksDrops;
    }

    public Config.MobsDrops getMobsDrops() {
        return mobsDrops;
    }

    public void setMobsDrops(Config.MobsDrops mobsDrops) {
        this.mobsDrops = mobsDrops;
    }

    public Config.FishingDrops getFishingDrops() {
        return fishingDrops;
    }

    public void setFishingDrops(Config.FishingDrops fishingDrops) {
        this.fishingDrops = fishingDrops;
    }

    public Config.BlockSpawners getBlockSpawners() {
        return blockSpawners;
    }

    public void setBlockSpawners(Config.BlockSpawners blockSpawners) {
        this.blockSpawners = blockSpawners;
    }

    public Config.FastHarvest getFastHarvest() {
        return fastHarvest;
    }

    public void setFastHarvest(Config.FastHarvest fastHarvest) {
        this.fastHarvest = fastHarvest;
    }

    public Config.CropsControl getCropsControl() {
        return cropsControl;
    }

    public void setCropsControl(Config.CropsControl cropsControl) {
        this.cropsControl = cropsControl;
    }

    public static BoxOUtils getInstance() {
        return instance;
    }

    public static void setInstance(BoxOUtils instance) {
        BoxOUtils.instance = instance;
    }
}
