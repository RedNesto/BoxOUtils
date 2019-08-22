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
package io.github.rednesto.bou.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.customdrops.CustomLootCommand;
import io.github.rednesto.bou.api.customdrops.ItemLoot;
import io.github.rednesto.bou.api.customdrops.MoneyLoot;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import io.github.rednesto.bou.api.requirement.Requirement;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomLootSerializer implements TypeSerializer<CustomLoot> {

    @Nullable
    @Override
    public CustomLoot deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        List<ItemLoot> itemLoots = new ArrayList<>(value.getNode("drops").getList(BouTypeTokens.ITEM_LOOT));
        itemLoots.removeIf(Objects::isNull);

        ConfigurationNode reuseNode = value.getNode("reuse");
        CustomLoot.Reuse reuse = !reuseNode.isVirtual() ? reuseNode.getValue(BouTypeTokens.CUSTOM_LOOT_REUSE) : null;

        ConfigurationNode requirementsNode = value.getNode("requirements");
        List<List<Requirement<?>>> requirements = RequirementSerializer.getRequirementGroups(requirementsNode);

        ConfigurationNode moneyNode = value.getNode("money");
        MoneyLoot moneyLoot = !moneyNode.isVirtual() ? moneyNode.getValue(BouTypeTokens.MONEY_LOOT) : null;

        IntQuantity experience = value.getNode("experience").getValue(BouTypeTokens.INT_QUANTITY);
        boolean overwrite = value.getNode("overwrite").getBoolean(false);
        boolean expOverwrite = value.getNode("exp-overwrite").getBoolean(false);

        List<CustomLootCommand> commands = new ArrayList<>(value.getNode("commands").getList(BouTypeTokens.CUSTOM_LOOT_COMMAND));
        commands.removeIf(Objects::isNull);

        return new CustomLoot(itemLoots, experience, overwrite, expOverwrite, requirements, moneyLoot, reuse, commands);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable CustomLoot obj, @NonNull ConfigurationNode value) {
        throw new UnsupportedOperationException();
    }
}
