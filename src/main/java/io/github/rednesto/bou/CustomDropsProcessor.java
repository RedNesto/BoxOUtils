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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.AffectEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;

public class CustomDropsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomDropsProcessor.class);

    public static void handleDropItemEvent(AffectEntityEvent event, CustomLoot customLoot, CustomLootProcessingContext context) {
        if (customLoot.isOverwrite()) {
            event.setCancelled(true);
            return;
        }

        CustomLoot.Reuse reuse = customLoot.getReuse();
        if (reuse != null) {
            if (!fulfillsRequirements(context.getSource(), event.getCause(), reuse.getRequirements())) {
                return;
            }

            List<? extends Entity> droppedItems = event.filterEntities(test -> !(test instanceof Item));
            if (droppedItems.isEmpty()) {
                return;
            }

            List<Entity> newDroppedItems = computeItemsReuse(droppedItems, reuse);
            event.getEntities().addAll(newDroppedItems);

            CustomLootRecipient recipient = customLoot.getRecipient();
            event.getEntities().removeIf(entity -> {
                ItemStack itemStack = entity.get(Keys.REPRESENTED_ITEM)
                        .map(ItemStackSnapshot::createStack)
                        .orElse(null);
                if (itemStack != null) {
                    recipient.receive(context, itemStack);
                    return true;
                }
                return false;
            });
        }
    }

    public static boolean fulfillsRequirements(Object source, Cause cause, List<List<Requirement<?>>> requirements) {
        if (requirements.isEmpty()) {
            return true;
        }

        for (List<Requirement<?>> requirementsList : requirements) {
            boolean failed = false;
            for (Requirement value : requirementsList) {
                try {
                    //noinspection unchecked
                    if (value.getApplicableType().isAssignableFrom(source.getClass()) && value.appliesTo(source, cause)) {
                        //noinspection unchecked
                        if (!value.fulfills(source, cause)) {
                            failed = true;
                            break;
                        }
                    }
                } catch (Throwable t) {
                    LOGGER.error("Failed to compute a requirement.", t);
                }
            }

            if (!failed) {
                return true;
            }
        }

        return false;
    }

    public static void dropLoot(CustomLootProcessingContext processingContext) {
        dropLoot(processingContext, true);
    }

    public static void dropLoot(CustomLootProcessingContext processingContext, boolean dropInWorld) {
        List<CustomLoot> loots = processingContext.getLoots();
        for (CustomLoot loot : loots) {
            for (CustomLootComponent components : loot.getComponents()) {
                try {
                    components.processLoot(processingContext);
                } catch (Throwable t) {
                    LOGGER.error("A CustomLootComponent encountered an exception during processing.", t);
                }
            }
        }

        Player targetPlayer = processingContext.getTargetPlayer();
        for (CustomLoot loot : loots) {
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

                loot.getRecipient().receive(processingContext, itemStack);
            }
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

    public static List<CustomLoot> getLootsToUse(List<CustomLoot> candidates, Object source, Cause cause) {
        List<CustomLoot> lootsToUse = new ArrayList<>();
        for (CustomLoot candidate : candidates) {
            if (fulfillsRequirements(source, cause, candidate.getRequirements())) {
                lootsToUse.add(candidate);
            }
        }
        return lootsToUse;
    }
}
