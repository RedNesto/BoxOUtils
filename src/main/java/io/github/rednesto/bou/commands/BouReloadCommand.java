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
package io.github.rednesto.bou.commands;

import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.SpongeConfig;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;

public class BouReloadCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        BoxOUtils plugin = BoxOUtils.getInstance();
        try {
            SpongeConfig.loadConf(plugin);
            plugin.getIntegrationsManager().reloadIntegrations(plugin);
            src.sendMessage(Text.of("Box O' Utils configuration has been reloaded successfully"));
        } catch (IOException e) {
            plugin.getLogger().error("An exception occurred when reloading configuration", e);
            if (!(src instanceof ConsoleSource)) {
                src.sendMessage(Text.of(TextColors.RED, "[Box O' Utils] Unable to reload configuration: " + e.getMessage()));
            }

            return CommandResult.empty();
        }

        return CommandResult.success();
    }

    public static CommandCallable create() {
        return CommandSpec.builder()
                .permission("boxoutils.reload")
                .executor(new BouReloadCommand())
                .build();
    }
}
