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
package io.github.rednesto.bou.sponge;

import io.github.rednesto.bou.common.CustomLoot;
import io.github.rednesto.bou.common.ItemLoot;
import io.github.rednesto.bou.common.MoneyLoot;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;

import javax.annotation.Nullable;

public class CustomDropsProcessor {

    public static void dropLoot(CustomLoot loot, @Nullable Player targetPlayer, @Nullable Location<World> targetLocation) {
        @Nullable MoneyLoot moneyLoot = loot.getMoneyLoot();
        if (targetPlayer != null && moneyLoot != null && moneyLoot.shouldLoot()) {
            int randomQuantity = moneyLoot.getAmount().getRandomQuantity();
            Sponge.getServiceManager().provide(EconomyService.class).ifPresent(economyService -> {
                UniqueAccount account = economyService.getOrCreateAccount(targetPlayer.getUniqueId()).orElse(null);
                if (account == null)
                    return;

                Currency usedCurrency;
                if (moneyLoot.getCurrencyId() == null) {
                    usedCurrency = economyService.getDefaultCurrency();
                } else {
                    usedCurrency = Sponge.getGame().getRegistry().getType(Currency.class, moneyLoot.getCurrencyId()).orElse(null);
                }

                if (usedCurrency == null)
                    return;

                Cause cause = Cause.of(EventContext.empty(), BoxOUtils.getInstance());
                TransactionResult transactionResult = account.deposit(usedCurrency, BigDecimal.valueOf(randomQuantity), cause);
                switch (transactionResult.getResult()) {
                    case ACCOUNT_NO_SPACE:
                        targetPlayer.sendMessage(Text.of(TextColors.RED,
                                "You do not have enough space in your account to earn ", transactionResult.getAmount(), " ", transactionResult.getCurrency().getDisplayName()));
                        break;
                    case FAILED:
                    case CONTEXT_MISMATCH:
                        targetPlayer.sendMessage(Text.of(TextColors.RED,
                                "Unable to add ", transactionResult.getAmount(), " ", transactionResult.getCurrency().getDisplayName(), " to your account"));
                        break;
                    case SUCCESS:
                        if (moneyLoot.getMessage() != null) {
                            String formattedAmount = TextSerializers.FORMATTING_CODE.serialize(transactionResult.getCurrency().format(transactionResult.getAmount()));
                            String message = moneyLoot.getMessage().replace("{money_amount}", formattedAmount);
                            targetPlayer.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message));
                        }
                        break;
                }
            });
        }

        if (targetLocation == null)
            return;

        if (loot.getExperience() > 0) {
            Entity experienceOrb = targetLocation.createEntity(EntityTypes.EXPERIENCE_ORB);
            experienceOrb.offer(Keys.CONTAINED_EXPERIENCE, loot.getExperience());
            targetLocation.spawnEntity(experienceOrb);
        }

        for (ItemLoot itemLoot : loot.getItemLoots()) {
            if (!itemLoot.shouldLoot())
                continue;

            Entity itemEntity = targetLocation.createEntity(EntityTypes.ITEM);
            ItemStack itemStack = IntegrationsManager.INSTANCE.createCustomDropStack(itemLoot.getProviderId(), itemLoot.getId(), targetPlayer).orElse(null);
            if (itemStack == null)
                continue;

            itemStack.setQuantity(itemLoot.getQuantityToLoot());

            if (itemLoot.getDisplayname() != null)
                itemStack.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(itemLoot.getDisplayname()));

            itemEntity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
            targetLocation.spawnEntity(itemEntity);
        }
    }
}
