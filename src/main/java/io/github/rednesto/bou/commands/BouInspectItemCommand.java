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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.spongepowered.api.command.args.GenericArguments.enumValue;

public class BouInspectItemCommand implements CommandExecutor {

    private static Map<DataType, Function<ItemStack, Iterable<Text>>> DATA_COLLECTORS;

    static {
        EnumMap<DataType, Function<ItemStack, Iterable<Text>>> collectors = new EnumMap<>(DataType.class);
        collectors.put(DataType.UNSAFE, BouInspectItemCommand::collectUnsafeData);
        collectors.put(DataType.SPONGE, BouInspectItemCommand::collectSpongeData);
        DATA_COLLECTORS = Collections.unmodifiableMap(collectors);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("This command must be executed by a player"));
            return CommandResult.empty();
        }

        ItemStack stack = ((Player) src).getItemInHand(HandTypes.MAIN_HAND).orElse(null);
        if (stack == null) {
            src.sendMessage(Text.of("You have nothing in your main hand"));
            return CommandResult.empty();
        }

        Function<ItemStack, Iterable<Text>> dataCollector = DATA_COLLECTORS.get(args.<DataType>requireOne("data type"));
        Sponge.getServiceManager().provideUnchecked(PaginationService.class).builder()
                .title(Text.of("Item in main hand"))
                .contents(dataCollector.apply(stack))
                .sendTo(src);

        return CommandResult.success();
    }

    private static Iterable<Text> collectUnsafeData(ItemStack stack) {
        DataContainer dataContainer = stack.toContainer();
        List<Text> lines = new ArrayList<>();
        for (Map.Entry<DataQuery, Object> entry : dataContainer.getValues(true).entrySet()) {
            Object value = entry.getValue();
            if (!(value instanceof Map)) {
                // Filter out parents, only display leaf nodes
                lines.add(Text.of(entry.getKey().asString('.'), " = ", value));
            }
        }
        return lines;
    }

    private static Iterable<Text> collectSpongeData(ItemStack stack) {
        return stack.getValues().stream()
                .map(value -> Text.of(value.getKey().getId(), " = ", value.get()))
                .collect(Collectors.toList());
    }

    public static CommandCallable create() {
        return CommandSpec.builder()
                .permission("boxoutils.inspect.item")
                .arguments(enumValue(Text.of("data type"), DataType.class))
                .executor(new BouInspectItemCommand())
                .build();
    }

    private enum DataType {
        UNSAFE,
        SPONGE
    }
}
