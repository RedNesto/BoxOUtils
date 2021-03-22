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

import io.github.rednesto.bou.BouUtils;
import io.github.rednesto.bou.api.customdrops.BasicCustomDropsProvider;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;

public class VanillaCustomDropsProvider extends BasicCustomDropsProvider {

    private static final DataQuery UNSAFE_DAMAGE_QUERY = DataQuery.of("UnsafeDamage");

    private final Integer unsafeDamage;

    public VanillaCustomDropsProvider(String itemId, @Nullable String displayname, double chance, @Nullable IntQuantity quantity,
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
                                                   String itemId,
                                                   @Nullable String displayname,
                                                   double chance,
                                                   @Nullable IntQuantity quantity) {
            Integer unsafeDamage = null;
            if (node != null) {
                ConfigurationNode unsafeDamageNode = node.getNode("unsafe-damage");
                if (!unsafeDamageNode.isVirtual()) {
                    unsafeDamage = unsafeDamageNode.getInt();
                }
            }
            return new VanillaCustomDropsProvider(itemId, displayname, chance, quantity, unsafeDamage);
        }

        @Override
        protected boolean isItemIdValid(String itemId) {
            return BouUtils.isNoSponge() || Sponge.getRegistry().getType(ItemType.class, itemId).isPresent();
        }

        @Override
        public String getId() {
            return ID;
        }
    }
}
