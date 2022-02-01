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

import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BouInspectItemCommand implements CommandExecutor {

    private static final Map<DataType, Function<ItemStack, Iterable<Component>>> DATA_COLLECTORS;

    static {
        EnumMap<DataType, Function<ItemStack, Iterable<Component>>> collectors = new EnumMap<>(DataType.class);
        collectors.put(DataType.UNSAFE, BouInspectItemCommand::collectUnsafeData);
        collectors.put(DataType.SPONGE, BouInspectItemCommand::collectSpongeData);
        DATA_COLLECTORS = Collections.unmodifiableMap(collectors);
    }

    private static final Parameter.Key<DataType> DATA_TYPE = Parameter.key("data type", DataType.class);

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        Player src = context.cause().first(Player.class)
                .orElseThrow(() -> new CommandException(Component.text("This command must be executed by a player")));
        ItemStack stack = src.itemInHand(HandTypes.MAIN_HAND);
        if (stack.isEmpty()) {
            throw new CommandException(Component.text("You must hold an item"));
        }

        Function<ItemStack, Iterable<Component>> dataCollector = DATA_COLLECTORS.get(context.requireOne(DATA_TYPE));
        Sponge.serviceProvider().paginationService().builder()
                .title(Component.text("Item in main hand"))
                .contents(dataCollector.apply(stack))
                .sendTo(src);

        return CommandResult.success();
    }

    private static Iterable<Component> collectUnsafeData(ItemStack stack) {
        DataContainer dataContainer = stack.toContainer();
        List<Component> lines = new ArrayList<>();
        for (Map.Entry<DataQuery, Object> entry : dataContainer.values(true).entrySet()) {
            Object value = entry.getValue();
            if (!(value instanceof Map)) {
                // Filter out parents, only display leaf nodes
                lines.add(Component.text(entry.getKey().asString('.') + " = " + value));
            }
        }
        return lines;
    }

    private static Iterable<Component> collectSpongeData(ItemStack stack) {
        return stack.getValues().stream()
                .map(value -> Component.text(value.key().key().value() + " = " + value.get()))
                .collect(Collectors.toList());
    }

    public static Command.Parameterized create() {
        Parameter.Value<DataType> dataTypeParam = Parameter.enumValue(DataType.class).key("data type").build();
        return Command.builder()
                .permission("boxoutils.inspect.item")
                .addParameter(dataTypeParam)
                .executor(new BouInspectItemCommand())
                .build();
    }

    private enum DataType {
        UNSAFE,
        SPONGE
    }
}
