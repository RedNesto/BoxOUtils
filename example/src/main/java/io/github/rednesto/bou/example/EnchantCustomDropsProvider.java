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

import io.github.rednesto.bou.sponge.BoxOUtils;
import io.github.rednesto.bou.sponge.CustomDropsProvider;
import io.github.rednesto.bou.sponge.IntegrationsManager;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nullable;

public class EnchantCustomDropsProvider implements CustomDropsProvider {

    private final BouIntegrationExample plugin;

    public EnchantCustomDropsProvider(BouIntegrationExample plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init(BoxOUtils plugin) {
        this.plugin.logger.info("Initializing EnchantCustomDropsProvider");
    }

    @Override
    public String getId() {
        return "auto-enchant";
    }

    @Override
    public Optional<ItemStack> createItemStack(String id, @Nullable Player targetPlayer) {
        return IntegrationsManager.INSTANCE.getDefaultCustomDropsProvider().createItemStack(id, targetPlayer)
                .map(item -> {
                    Enchantment enchant = Enchantment.of(EnchantmentTypes.KNOCKBACK, 1);
                    item.offer(Keys.ITEM_ENCHANTMENTS, Collections.singletonList(enchant));

                    return item;
                });
    }
}
