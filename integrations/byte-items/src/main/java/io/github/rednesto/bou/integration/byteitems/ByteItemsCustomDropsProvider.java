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
package io.github.rednesto.bou.integration.byteitems;

import de.randombyte.byteitems.api.ByteItemsService;
import io.github.rednesto.bou.api.customdrops.BasicCustomDropsProvider;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nullable;

public class ByteItemsCustomDropsProvider extends BasicCustomDropsProvider {

    public ByteItemsCustomDropsProvider(String itemId,
                                           @Nullable String displayname,
                                           double chance,
                                           @Nullable IntQuantity quantity) {
        super(itemId, displayname, chance, quantity);
    }

    @Nullable
    @Override
    protected ItemStack createStack(CustomLootProcessingContext context, String itemId) {
        return Sponge.getServiceManager().provide(ByteItemsService.class)
                .flatMap(service -> service.get(itemId))
                .map(ItemStackSnapshot::createStack)
                .orElse(null);
    }

    public static class Factory extends BasicFactory {

        @Override
        protected BasicCustomDropsProvider provide(@Nullable ConfigurationNode node,
                                                   String itemId,
                                                   @Nullable String displayname,
                                                   double chance,
                                                   @Nullable IntQuantity quantity) {
            return new ByteItemsCustomDropsProvider(itemId, displayname, chance, quantity);
        }

        @Override
        public String getId() {
            return "box-o-utils:byte-items";
        }
    }
}
