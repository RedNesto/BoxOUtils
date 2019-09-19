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
package io.github.rednesto.bou.integration.customdrops;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.api.customdrops.*;
import io.github.rednesto.bou.config.serializers.BouTypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.math.BigDecimal;

public class MoneyLootComponent implements CustomLootComponent {

    private final MoneyLoot moneyLoot;

    public MoneyLootComponent(MoneyLoot moneyLoot) {
        this.moneyLoot = moneyLoot;
    }

    @Override
    public void processLoot(CustomLootProcessingContext processingContext) {
        Player targetPlayer = processingContext.getTargetPlayer();
        if (targetPlayer == null || !moneyLoot.shouldLoot()) {
            return;
        }

        int randomQuantity = moneyLoot.getAmount().get();
        Sponge.getServiceManager().provide(EconomyService.class).ifPresent(economyService -> {
            UniqueAccount account = economyService.getOrCreateAccount(targetPlayer.getUniqueId()).orElse(null);
            if (account == null) {
                return;
            }

            Currency usedCurrency;
            if (moneyLoot.getCurrencyId() == null) {
                usedCurrency = economyService.getDefaultCurrency();
            } else {
                usedCurrency = Sponge.getGame().getRegistry().getType(Currency.class, moneyLoot.getCurrencyId()).orElse(null);
            }

            if (usedCurrency == null) {
                return;
            }

            Cause depositCause = Cause.of(EventContext.empty(), BoxOUtils.getInstance());
            TransactionResult transactionResult = account.deposit(usedCurrency, BigDecimal.valueOf(randomQuantity), depositCause);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MoneyLootComponent)) {
            return false;
        }

        MoneyLootComponent that = (MoneyLootComponent) o;
        return moneyLoot.equals(that.moneyLoot);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("moneyLoot", moneyLoot)
                .toString();
    }

    public static class Provider implements CustomLootComponentProvider {

        @Override
        public CustomLootComponent provide(ConfigurationNode node) throws CustomLootComponentConfigurationException {
            try {
                MoneyLoot moneyLoot = node.getValue(BouTypeTokens.MONEY_LOOT);
                if (moneyLoot == null) {
                    throw new CustomLootComponentConfigurationException("Could not deserialize a MoneyLoot.");
                }

                return new MoneyLootComponent(moneyLoot);
            } catch (ObjectMappingException e) {
                throw new CustomLootComponentConfigurationException(e);
            }
        }

        @Override
        public String getId() {
            return "box-o-utils:money";
        }
    }
}
