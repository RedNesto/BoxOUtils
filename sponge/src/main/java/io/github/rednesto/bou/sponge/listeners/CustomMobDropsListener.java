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
package io.github.rednesto.bou.sponge.listeners;

import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.CustomLoot;
import io.github.rednesto.bou.common.MoneyLoot;
import io.github.rednesto.bou.sponge.BoxOUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.Optional;

public class CustomMobDropsListener {

    @Listener
    public void onMobDeath(DestructEntityEvent.Death event) {
        CustomLoot loot = Config.CUSTOM_MOBS_DROPS.get(event.getTargetEntity().getType().getId());

        if(loot != null) {
            if(loot.getExperience() > 0) {
                World world = event.getTargetEntity().getWorld();
                Entity experienceOrb = world.createEntity(EntityTypes.EXPERIENCE_ORB, event.getTargetEntity().getLocation().getPosition());
                experienceOrb.offer(Keys.CONTAINED_EXPERIENCE, loot.getExperience());
                world.spawnEntity(experienceOrb);
            }

            Player player = event.getCause().first(Player.class).orElse(null);
            if (player == null) {
                Optional<IndirectEntityDamageSource> maybeIndirectSource = event.getCause().first(IndirectEntityDamageSource.class);
                if (maybeIndirectSource.isPresent()) {
                    Entity indirectSource = maybeIndirectSource.get().getIndirectSource();
                    if (indirectSource instanceof Player) {
                        player = (Player) indirectSource;
                    }
                }
            }

            Player finalPlayer = player;
            if (finalPlayer != null && loot.getMoneyLoot() != null && loot.getMoneyLoot().shouldLoot()) {
                MoneyLoot moneyLoot = loot.getMoneyLoot();
                int randomQuantity = moneyLoot.getAmount().getRandomQuantity();
                Sponge.getServiceManager().provide(EconomyService.class).ifPresent(economyService -> {
                    Optional<UniqueAccount> maybeAccount = economyService.getOrCreateAccount(finalPlayer.getUniqueId());
                    if (!maybeAccount.isPresent()) {
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

                    Cause cause = Cause.of(EventContext.empty(), BoxOUtils.getInstance());
                    TransactionResult transactionResult = maybeAccount.get().deposit(usedCurrency, BigDecimal.valueOf(randomQuantity), cause);                    switch (transactionResult.getResult()) {
                        case ACCOUNT_NO_SPACE:
                            finalPlayer
                                    .sendMessage(Text.of(TextColors.RED, "You do not have enough space in your account to earn ", transactionResult.getAmount(), " ", transactionResult.getCurrency().getDisplayName()));
                            break;
                        case FAILED:
                        case CONTEXT_MISMATCH:
                            finalPlayer.sendMessage(Text.of(TextColors.RED, "Unable to add ", transactionResult.getAmount(), " ", transactionResult.getCurrency().getDisplayName(), " to your account"));
                            break;
                        case SUCCESS:
                            if (moneyLoot.getMessage() != null) {
                                String formattedAmount = TextSerializers.FORMATTING_CODE.serialize(transactionResult.getCurrency().format(transactionResult.getAmount()));
                                String message = moneyLoot.getMessage().replace("{money_amount}", formattedAmount);
                                finalPlayer.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message));
                            }
                            break;
                    }
                });
            }

            loot.getItemLoots().forEach(itemLoot -> {
                switch(itemLoot.getType()) {
                    case CLASSIC:
                        if(itemLoot.shouldLoot()) {
                            Sponge.getRegistry().getType(ItemType.class, itemLoot.getId()).ifPresent(itemType -> {
                                Entity entity = event.getTargetEntity().getWorld().createEntity(EntityTypes.ITEM, event.getTargetEntity().getLocation().getPosition());
                                ItemStack itemStack = ItemStack.of(itemType, itemLoot.getQuantityToLoot());
                                if (itemLoot.getDisplayname() != null)
                                    itemStack.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(itemLoot.getDisplayname()));

                                entity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
                                event.getTargetEntity().getWorld().spawnEntity(entity);
                            });
                        }
                        break;
                    case FILE_INVENTORIES:
                        if (finalPlayer != null) {
                            BoxOUtils.getInstance().fileInvDo(integration -> integration.spawnMobDrop(itemLoot, finalPlayer, event.getTargetEntity()));
                        }
                        break;
                }
            });
        }
    }

    @Listener
    public void onItemDrop(DropItemEvent.Destruct event) {
        event.getCause().first(Entity.class).ifPresent(entity -> {
            if(Config.CUSTOM_MOBS_DROPS.containsKey(entity.getType().getId()) && Config.CUSTOM_MOBS_DROPS.get(entity.getType().getId()).isOverwrite())
                event.setCancelled(true);
        });
    }

    @Listener
    public void onExpOrbSpawn(SpawnEntityEvent event) {
        event.getEntities().forEach(entity -> {
            if(entity instanceof ExperienceOrb) {
                event.getCause().noneOf(ExperienceOrb.class).forEach(it -> {
                    if(it instanceof Entity) {
                        CustomLoot customLoot = Config.CUSTOM_MOBS_DROPS.get(((Entity) it).getType().getId());
                        if (customLoot != null && customLoot.isExpOverwrite()) {
                            event.setCancelled(true);
                        }
                    }
                });
            }
        });
    }
}
