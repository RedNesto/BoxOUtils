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
package io.github.rednesto.bou.example;

import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.api.customdrops.BasicCustomDropsProvider;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collections;

public class EnchantCustomDropsProvider extends BasicCustomDropsProvider {

    public EnchantCustomDropsProvider(ResourceKey itemId,
                                      @Nullable String displayname,
                                      double chance,
                                      @Nullable IntQuantity quantity) {
        super(itemId, displayname, chance, quantity);
    }

    @Override
    protected ItemStack transform(CustomLootProcessingContext context, ItemStack stack) {
        Enchantment enchant = Enchantment.of(EnchantmentTypes.KNOCKBACK, 1);
        stack.offer(Keys.APPLIED_ENCHANTMENTS, Collections.singletonList(enchant));
        return stack;
    }

    public static class Factory extends BasicFactory {

        private final BouIntegrationExample plugin;

        public Factory(BouIntegrationExample plugin) {
            this.plugin = plugin;
        }

        @Override
        protected BasicCustomDropsProvider provide(@Nullable ConfigurationNode node,
                                                   ResourceKey itemId,
                                                   @Nullable String displayname,
                                                   double chance,
                                                   @Nullable IntQuantity quantity) {
            return new EnchantCustomDropsProvider(itemId, displayname, chance, quantity);
        }

        @Override
        public void init(BoxOUtils plugin) {
            this.plugin.logger.info("Initializing EnchantCustomDropsProvider.Factory");
        }

        @Override
        public String getId() {
            return "bou-integration-example:auto-enchant";
        }
    }
}
