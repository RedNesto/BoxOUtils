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
package io.github.rednesto.bou.integration.customdrops.recipients;

import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.customdrops.CustomLootRecipient;
import io.github.rednesto.bou.api.customdrops.CustomLootRecipientProvider;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;

public class PlayerInventoryLootRecipient implements CustomLootRecipient {

    public static final PlayerInventoryLootRecipient INSTANCE = new PlayerInventoryLootRecipient();

    private PlayerInventoryLootRecipient() {}

    @Override
    public void receive(CustomLootProcessingContext context, ItemStack stack) {
        Player targetPlayer = context.getTargetPlayer();
        if (targetPlayer != null) {
            targetPlayer.getInventory().offer(stack);
        } else {
            ContextLocationLootRecipient.INSTANCE.receive(context, stack);
        }
    }

    public static class Provider implements CustomLootRecipientProvider {

        @Override
        public CustomLootRecipient provide(@Nullable ConfigurationNode node) {
            return INSTANCE;
        }

        @Override
        public String getId() {
            return "box-o-utils:player-inventory";
        }
    }
}
