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
package io.github.rednesto.bou.integration.customdrops;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.CustomDropsProcessor;
import io.github.rednesto.bou.api.customdrops.CustomLootCommand;
import io.github.rednesto.bou.api.customdrops.CustomLootComponent;
import io.github.rednesto.bou.api.customdrops.CustomLootComponentProvider;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.config.serializers.BouTypeTokens;
import io.github.rednesto.bou.config.serializers.SerializerUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

public class CommandLootComponent implements CustomLootComponent {

    private final List<CustomLootCommand> commands;

    public CommandLootComponent(List<CustomLootCommand> commands) {
        this.commands = commands;
    }

    @Override
    public void processLoot(CustomLootProcessingContext processingContext) {
        Player targetPlayer = processingContext.getTargetPlayer();
        if (targetPlayer == null) {
            return;
        }

        commands.forEach(command -> {
            if (!command.shouldExecute() || !CustomDropsProcessor.fulfillsRequirements(processingContext, command.getRequirements())) {
                return;
            }

            String commandToSend = command.getRawCommand()
                    .replace("{player.name}", targetPlayer.getName())
                    .replace("{player.uuid}", targetPlayer.getUniqueId().toString());
            CommandSource commandSource = null;
            switch (command.getSenderMode()) {
                case SERVER:
                    commandSource = Sponge.getServer().getConsole();
                    break;
                case PLAYER:
                    commandSource = targetPlayer;
                    break;
            }

            Sponge.getCommandManager().process(commandSource, commandToSend);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandLootComponent)) {
            return false;
        }

        CommandLootComponent that = (CommandLootComponent) o;
        return commands.equals(that.commands);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("commands", commands)
                .toString();
    }

    public static class Provider implements CustomLootComponentProvider {

        @Override
        public CustomLootComponent provide(ConfigurationNode node) throws ObjectMappingException {
            return new CommandLootComponent(SerializerUtils.getListSafe(node, BouTypeTokens.CUSTOM_LOOT_COMMAND));
        }

        @Override
        public String getId() {
            return "box-o-utils:commands";
        }
    }
}
