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
import io.github.rednesto.bou.api.customdrops.*;
import io.github.rednesto.bou.config.serializers.BouTypeTokens;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.math.BigDecimal;

public class MoneyLootComponent implements CustomLootComponent {

    private final MoneyLoot moneyLoot;

    public MoneyLootComponent(MoneyLoot moneyLoot) {
        this.moneyLoot = moneyLoot;
    }

    @Override
    public void processLoot(CustomLootProcessingContext processingContext) {
        @Nullable ServerPlayer targetPlayer = processingContext.getTargetPlayer();
        if (targetPlayer == null || !moneyLoot.shouldLoot()) {
            return;
        }

        int randomQuantity = moneyLoot.getAmount().get();
        Sponge.server().serviceProvider().economyService().ifPresent(economyService -> {
            @Nullable UniqueAccount account = economyService.findOrCreateAccount(targetPlayer.uniqueId()).orElse(null);
            if (account == null) {
                return;
            }

            @Nullable Currency usedCurrency;
            if (moneyLoot.getCurrencyId() == null) {
                usedCurrency = economyService.defaultCurrency();
            } else {
                usedCurrency = Sponge.server().registry(RegistryTypes.CURRENCY).findValue(moneyLoot.getCurrencyId()).orElse(null);
            }

            if (usedCurrency == null) {
                return;
            }

            TransactionResult transactionResult = account.deposit(usedCurrency, BigDecimal.valueOf(randomQuantity));
            switch (transactionResult.result()) {
                case ACCOUNT_NO_SPACE:
                    String message = String.format("You do not have enough space in your account to earn %s %s",
                            transactionResult.amount(), transactionResult.currency().displayName());
                    targetPlayer.sendMessage(Component.text(message, NamedTextColor.RED));
                    break;
                case FAILED:
                case CONTEXT_MISMATCH:
                    message = String.format("Unable to add %s %s to your account",
                            transactionResult.amount(), transactionResult.currency().displayName());
                    targetPlayer.sendMessage(Component.text(message, NamedTextColor.RED));
                    break;
                case SUCCESS:
                    if (moneyLoot.getMessage() != null) {
                        String formattedAmount = LegacyComponentSerializer.legacyAmpersand()
                                .serialize(transactionResult.currency().format(transactionResult.amount()));
                        message = moneyLoot.getMessage().replace("{money_amount}", formattedAmount);
                        targetPlayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
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
                @Nullable MoneyLoot moneyLoot = node.get(BouTypeTokens.MONEY_LOOT);
                if (moneyLoot == null) {
                    throw new CustomLootComponentConfigurationException("Could not deserialize a MoneyLoot.");
                }

                return new MoneyLootComponent(moneyLoot);
            } catch (SerializationException e) {
                throw new CustomLootComponentConfigurationException(e);
            }
        }

        @Override
        public String getId() {
            return "box-o-utils:money";
        }
    }
}
