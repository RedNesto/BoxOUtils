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
package io.github.rednesto.bou;

import io.github.rednesto.bou.api.customdrops.*;
import io.github.rednesto.bou.api.lootReuse.LootReuse;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import io.github.rednesto.bou.api.requirement.Requirement;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.AffectEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public class CustomDropsProcessor {

    public static void handleDropItemEvent(AffectEntityEvent event, CustomLoot customLoot, Object source) {
        if (customLoot.isOverwrite()) {
            event.setCancelled(true);
            return;
        }

        CustomLoot.Reuse reuse = customLoot.getReuse();
        if (reuse != null) {
            if (!fulfillsRequirements(source, event.getCause(), reuse.getRequirements())) {
                return;
            }

            List<? extends Entity> droppedItems = event.filterEntities(test -> !(test instanceof Item));
            if (droppedItems.isEmpty()) {
                return;
            }

            List<Entity> newDroppedItems = computeItemsReuse(droppedItems, reuse);
            event.getEntities().addAll(newDroppedItems);
        }
    }

    public static boolean fulfillsRequirements(Object source, Cause cause, List<List<Requirement<?>>> requirements) {
        if (requirements.isEmpty()) {
            return true;
        }

        for (List<Requirement<?>> requirementsList : requirements) {
            boolean failed = false;
            for (Requirement value : requirementsList) {
                //noinspection unchecked
                if (value.getApplicableType().isAssignableFrom(source.getClass()) && value.appliesTo(source, cause)) {
                    //noinspection unchecked
                    if (!value.fulfills(source, cause)) {
                        failed = true;
                        break;
                    }
                }
            }

            if (!failed) {
                return true;
            }
        }

        return false;
    }

    public static void dropLoot(CustomLoot loot, @Nullable Player targetPlayer, @Nullable Location<World> targetLocation,
                                Object source, Cause cause) {
        dropLoot(loot, targetPlayer, targetLocation, source, cause, true);
    }

    public static void dropLoot(CustomLoot loot, @Nullable Player targetPlayer, @Nullable Location<World> targetLocation,
                                Object source, Cause cause, boolean dropInWorld) {
        @Nullable MoneyLoot moneyLoot = loot.getMoneyLoot();
        if (targetPlayer != null && moneyLoot != null && moneyLoot.shouldLoot()) {
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

        if (targetPlayer != null) {
            for (CustomLootCommand command : loot.getCommands()) {
                if (!command.shouldExecute() || !fulfillsRequirements(source, cause, command.getRequirements())) {
                    continue;
                }

                String commandToSend = command.getRawCommand()
                        .replace("{player.name}", targetPlayer.getName())
                        .replace("{player.uuid}", targetPlayer.getUniqueId().toString());
                CommandSource commandSource = null;
                switch (command.getSenderMode()) {
                    case SERVER:
                        commandSource = Sponge.getServer().getConsole();
                        break;
                    case PLAYER:
                        commandSource = targetPlayer;
                        break;
                }

                Sponge.getCommandManager().process(commandSource, commandToSend);
            }
        }

        if (targetLocation != null) {
            IntQuantity experience = loot.getExperience();
            if (experience != null) {
                int experienceAmount = experience.get();
                if (experienceAmount > 0) {
                    Entity experienceOrb = targetLocation.createEntity(EntityTypes.EXPERIENCE_ORB);
                    experienceOrb.offer(Keys.CONTAINED_EXPERIENCE, experienceAmount);
                    targetLocation.spawnEntity(experienceOrb);
                }
            }
        }

        Consumer<ItemStack> lootStackConsumer;
        if (dropInWorld) {
            if (targetLocation == null) {
                return;
            }

            lootStackConsumer = itemStack -> {
                Entity itemEntity = targetLocation.createEntity(EntityTypes.ITEM);
                itemEntity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
                targetLocation.spawnEntity(itemEntity);
            };
        } else if (targetPlayer != null) {
            lootStackConsumer = itemStack -> targetPlayer.getInventory().offer(itemStack);
        } else {
            return;
        }

        for (ItemLoot itemLoot : loot.getItemLoots()) {
            if (!itemLoot.shouldLoot()) {
                continue;
            }

            ItemStack itemStack = CustomDropsProviderIntegrations.getInstance()
                    .createCustomDropStack(itemLoot.getProviderId(), itemLoot.getId(), targetPlayer)
                    .orElse(null);
            if (itemStack == null) {
                continue;
            }

            IntQuantity quantity = itemLoot.getQuantity();
            if (quantity != null) {
                itemStack.setQuantity(quantity.get());
            }

            if (itemLoot.getDisplayname() != null) {
                itemStack.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(itemLoot.getDisplayname()));
            }

            lootStackConsumer.accept(itemStack);
        }
    }

    public static List<Entity> computeItemsReuse(List<? extends Entity> originalDroppedItems, CustomLoot.Reuse reuse) {
        ArrayList<Entity> result = new ArrayList<>();
        ArrayList<ItemStack> reuseResult = new ArrayList<>();
        for (Entity itemEntity : originalDroppedItems) {
            ItemStackSnapshot originalItem = itemEntity.get(Keys.REPRESENTED_ITEM).orElse(null);
            if (originalItem == null) {
                continue;
            }

            computeSingleItemReuse(reuse, reuseResult, originalItem.createStack());
            if (reuseResult.isEmpty()) {
                continue;
            }

            if (reuseResult.size() == 1) {
                ItemStackSnapshot itemSnapshot = reuseResult.get(0).createSnapshot();
                itemEntity.offer(Keys.REPRESENTED_ITEM, itemSnapshot);
                result.add(itemEntity);
            } else {
                reuseResult.forEach(itemStack -> {
                    ItemStackSnapshot itemSnapshot = itemStack.createSnapshot();
                    Entity entityCopy = ((Entity) itemEntity.copy());
                    entityCopy.offer(Keys.REPRESENTED_ITEM, itemSnapshot);
                    result.add(entityCopy);
                });
            }

            reuseResult.clear();
        }

        return result;
    }

    public static void computeSingleItemReuse(CustomLoot.Reuse reuse, List<ItemStack> result, ItemStack originalItem) {
        int reuseQuantity;
        LootReuse lootReuse = reuse.getItems().get(originalItem.getType().getId());
        if (lootReuse != null) {
            reuseQuantity = lootReuse.computeQuantity(originalItem.getQuantity());
        } else {
            reuseQuantity = Math.round(originalItem.getQuantity() * reuse.getMultiplier());
        }

        if (reuseQuantity <= 0) {
            return;
        }

        if (reuseQuantity == originalItem.getQuantity()) {
            result.add(originalItem);
            return;
        }

        int maxStackQuantity = originalItem.getType().getMaxStackQuantity();
        if (reuseQuantity <= maxStackQuantity) {
            originalItem.setQuantity(reuseQuantity);
            result.add(originalItem);
        } else {
            int restQuantity = reuseQuantity % maxStackQuantity;
            int fullStacksCount = (reuseQuantity - restQuantity) / maxStackQuantity;
            for (int i = 0; i < fullStacksCount; i++) {
                ItemStack stack = originalItem.copy();
                stack.setQuantity(maxStackQuantity);
                result.add(stack);
            }

            originalItem.setQuantity(restQuantity);
            result.add(originalItem);
        }
    }
}
