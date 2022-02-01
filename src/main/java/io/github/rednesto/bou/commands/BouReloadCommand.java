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
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

import java.io.IOException;

public class BouReloadCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        BoxOUtils plugin = BoxOUtils.getInstance();
        try {
            SpongeConfig.loadConf(plugin);
            plugin.getIntegrationsManager().reloadIntegrations(plugin);
            context.sendMessage(Identity.nil(), Component.text("Box O' Utils configuration has been reloaded successfully"));
        } catch (IOException e) {
            plugin.getLogger().error("An exception occurred when reloading configuration", e);
            return CommandResult.error(Component.text("[Box O' Utils] Unable to reload configuration: " + e.getMessage(), NamedTextColor.RED));
        }

        return CommandResult.success();
    }

    public static Command.Parameterized create() {
        return Command.builder()
                .permission("boxoutils.reload")
                .executor(new BouReloadCommand())
                .build();
    }
}
