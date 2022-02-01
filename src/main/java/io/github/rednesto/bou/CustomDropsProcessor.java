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

import io.github.rednesto.bou.api.BouEventContextKeys;
import io.github.rednesto.bou.api.customdrops.*;
import io.github.rednesto.bou.api.lootReuse.LootReuse;
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.integration.customdrops.recipients.ContextLocationLootRecipient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.entity.AffectEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class CustomDropsProcessor {

    private static final Logger LOGGER = LogManager.getLogger(CustomDropsProcessor.class);

    public static void handleDropItemEvent(AffectEntityEvent event, CustomLoot customLoot, CustomLootProcessingContext context) {
        if (customLoot.isOverwrite()) {
            event.setCancelled(true);
            return;
        }

        CustomLoot.@Nullable Reuse reuse = customLoot.getReuse();
        List<Entity> additionalItems = Collections.emptyList();
        if (reuse != null && !event.context().get(BouEventContextKeys.IS_REUSE_DROPS).orElse(false)) {
            if (!fulfillsRequirements(context, reuse.getRequirements())) {
                return;
            }

            List<? extends Entity> spawnedEntities = event.entities();
            if (spawnedEntities.isEmpty()) {
                return;
            }

            additionalItems = computeItemsReuse(spawnedEntities, reuse);
        }

        CustomLootRecipient recipient = customLoot.getRecipient();
        if (customLoot.isRedirectBaseDropsToRecipient() && !recipient.equals(ContextLocationLootRecipient.INSTANCE)) {
            event.filterEntities(entity -> {
                @Nullable ItemStack itemStack = entity.get(Keys.ITEM_STACK_SNAPSHOT)
                        .map(ItemStackSnapshot::createStack)
                        .orElse(null);
                if (itemStack != null) {
                    recipient.receive(context, itemStack);
                    return false;
                }
                return true;
            });
            try (CauseStackManager.StackFrame stackFrame = Sponge.server().causeStackManager().pushCauseFrame()) {
                stackFrame.addContext(BouEventContextKeys.IS_REUSE_DROPS, true);
                for (Entity additionalItem : additionalItems) {
                    additionalItem.get(Keys.ITEM_STACK_SNAPSHOT)
                            .map(ItemStackSnapshot::createStack)
                            .ifPresent(itemStack -> recipient.receive(context, itemStack));
                }
            }
        } else {
            try (CauseStackManager.StackFrame stackFrame = Sponge.server().causeStackManager().pushCauseFrame()) {
                stackFrame.addContext(BouEventContextKeys.IS_REUSE_DROPS, true);
                context.getTargetLocation().world().spawnEntities(additionalItems);
            }
        }
    }

    public static boolean fulfillsRequirements(CustomLootProcessingContext context, List<List<Requirement>> requirements) {
        if (requirements.isEmpty()) {
            return true;
        }

        for (List<Requirement> requirementsList : requirements) {
            boolean failed = false;
            for (Requirement value : requirementsList) {
                try {
                    if (value.appliesTo(context) && !value.fulfills(context)) {
                        failed = true;
                        break;
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

    public static void dropLoot(CustomLootProcessingContext context) {
        processLoots(context, (loot, itemStack) -> loot.getRecipient().receive(context, itemStack));
    }

    public static void processLoots(CustomLootProcessingContext context, BiConsumer<CustomLoot, ItemStack> dropsConsumer) {
        List<CustomLoot> loots = context.getLoots();
        for (CustomLoot loot : loots) {
            for (CustomLootComponent components : loot.getComponents()) {
                try {
                    components.processLoot(context);
                } catch (Throwable t) {
                    LOGGER.error("A CustomLootComponent encountered an exception during processing.", t);
                }
            }

            for (CustomDropsProvider dropsProvider : loot.getDropsProviders()) {
                try {
                    Collection<ItemStack> stacks = dropsProvider.createStacks(context);
                    for (ItemStack stack : stacks) {
                        dropsConsumer.accept(loot, stack);
                    }
                } catch (Throwable t) {
                    LOGGER.error("Unhandled error throw by CustomDropsProvider {} when creating stacks.", dropsProvider, t);
                }
            }
        }
    }

    public static List<Entity> computeItemsReuse(List<? extends Entity> spawnedEntities, CustomLoot.Reuse reuse) {
        ArrayList<Entity> additionalItems = new ArrayList<>();
        ArrayList<ItemStack> reuseResult = new ArrayList<>();
        for (Entity spawnedEntity : spawnedEntities) {
            @Nullable ItemStackSnapshot originalItem = spawnedEntity.get(Keys.ITEM_STACK_SNAPSHOT).orElse(null);
            if (originalItem == null) {
                continue;
            }

            computeSingleItemReuse(reuse, reuseResult, originalItem.createStack());
            if (reuseResult.isEmpty()) {
                continue;
            }

            ItemStackSnapshot itemSnapshot = reuseResult.get(0).createSnapshot();
            spawnedEntity.offer(Keys.ITEM_STACK_SNAPSHOT, itemSnapshot);

            if (reuseResult.size() > 1) {
                reuseResult.remove(0);
                reuseResult.forEach(itemStack -> {
                    ItemStackSnapshot additionalItemStack = itemStack.createSnapshot();
                    Entity entityCopy = spawnedEntity.copy();
                    entityCopy.offer(Keys.ITEM_STACK_SNAPSHOT, additionalItemStack);
                    additionalItems.add(entityCopy);
                });
            }

            reuseResult.clear();
        }

        return additionalItems;
    }

    public static void computeSingleItemReuse(CustomLoot.Reuse reuse, List<ItemStack> result, ItemStack originalItem) {
        int reuseQuantity;
        LootReuse lootReuse = reuse.getItems().get(RegistryTypes.ITEM_TYPE.keyFor(Sponge.game(), originalItem.type()));
        if (lootReuse != null) {
            reuseQuantity = lootReuse.computeQuantity(originalItem.quantity());
        } else {
            reuseQuantity = Math.round(originalItem.quantity() * reuse.getMultiplier());
        }

        if (reuseQuantity <= 0) {
            return;
        }

        if (reuseQuantity == originalItem.quantity()) {
            result.add(originalItem);
            return;
        }

        int maxStackQuantity = originalItem.type().maxStackQuantity();
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

    public static List<CustomLoot> getLootsToUse(List<CustomLoot> candidates, CustomLootProcessingContext context) {
        List<CustomLoot> lootsToUse = new ArrayList<>();
        for (CustomLoot candidate : candidates) {
            if (candidate.shouldLoot() && fulfillsRequirements(context, candidate.getRequirements())) {
                lootsToUse.add(candidate);
            }
        }
        return lootsToUse;
    }
}
