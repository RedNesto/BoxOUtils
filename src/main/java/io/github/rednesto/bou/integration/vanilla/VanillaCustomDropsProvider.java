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
package io.github.rednesto.bou.integration.vanilla;

import io.github.rednesto.bou.api.customdrops.BasicCustomDropsProvider;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

public class VanillaCustomDropsProvider extends BasicCustomDropsProvider {

    private static final DataQuery UNSAFE_DAMAGE_QUERY = DataQuery.of("UnsafeDamage");

    private final @Nullable Integer unsafeDamage;

    public VanillaCustomDropsProvider(ResourceKey itemId, @Nullable String displayname, double chance, @Nullable IntQuantity quantity,
                                      @Nullable Integer unsafeDamage) {
        super(itemId, displayname, chance, quantity);
        this.unsafeDamage = unsafeDamage;
    }

    @Override
    protected ItemStack transform(CustomLootProcessingContext context, ItemStack stack) {
        if (this.unsafeDamage == null) {
            return stack;
        }

        DataContainer transformed = stack.toContainer();
        transformed.set(UNSAFE_DAMAGE_QUERY, this.unsafeDamage);
        return ItemStack.builder().fromContainer(transformed).build();
    }

    public static class Factory extends BasicFactory {

        public static final String ID = "box-o-utils:vanilla";

        @Override
        protected BasicCustomDropsProvider provide(@Nullable ConfigurationNode node,
                                                   ResourceKey itemId,
                                                   @Nullable String displayname,
                                                   double chance,
                                                   @Nullable IntQuantity quantity) {
            @Nullable Integer unsafeDamage = null;
            if (node != null) {
                ConfigurationNode unsafeDamageNode = node.node("unsafe-damage");
                if (!unsafeDamageNode.virtual()) {
                    unsafeDamage = unsafeDamageNode.getInt();
                }
            }
            return new VanillaCustomDropsProvider(itemId, displayname, chance, quantity, unsafeDamage);
        }

        @Override
        public String getId() {
            return ID;
        }
    }
}
