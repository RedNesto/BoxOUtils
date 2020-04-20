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
package io.github.rednesto.bou.listeners;

import io.github.rednesto.bou.Config;
import io.github.rednesto.bou.CustomDropsProcessor;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CustomFishingDropsListener {

    @Listener
    public void onFishingDrop(FishingEvent.Stop event) {
        List<Transaction<ItemStackSnapshot>> transactions = event.getTransactions();
        if (transactions.isEmpty()) {
            return;
        }

        Config.FishingDrops fishingDrops = Config.getFishingDrops();
        if (!fishingDrops.enabled) {
            return;
        }

        List<CustomLoot> loots = CustomDropsProcessor.getLootsToUse(fishingDrops.loots, event.getSource(), event.getCause());
        if (loots.isEmpty()) {
            return;
        }

        boolean overwritten = processOverwrite(transactions, loots);
        if (!overwritten) {
            processReuse(transactions, loots);
        }

        Player player = event.getCause().first(Player.class).orElse(null);
        Location<World> location = event.getFishHook().getLocation();
        CustomLootProcessingContext context = new CustomLootProcessingContext(loots, event, event.getFishHook(), event.getCause(), player, location);
        CustomDropsProcessor.processLoots(context, (loot, itemStack) -> transactions.add(new Transaction<>(ItemStackSnapshot.NONE, itemStack.createSnapshot())));
    }

    private boolean processOverwrite(List<Transaction<ItemStackSnapshot>> transactions, List<CustomLoot> loots) {
        for (CustomLoot loot : loots) {
            if (loot.isOverwrite()) {
                for (Transaction<ItemStackSnapshot> transaction : transactions) {
                    transaction.setValid(false);
                }
                return true;
            }
        }
        return false;
    }

    private static void processReuse(List<Transaction<ItemStackSnapshot>> transactions, List<CustomLoot> loots) {
        for (CustomLoot loot : loots) {
            CustomLoot.Reuse reuse = loot.getReuse();
            if (reuse == null) {
                continue;
            }

            ListIterator<Transaction<ItemStackSnapshot>> transactionsIterator = transactions.listIterator();
            while (transactionsIterator.hasNext()) {
                Transaction<ItemStackSnapshot> transaction = transactionsIterator.next();
                List<ItemStack> itemsResult = new ArrayList<>();
                CustomDropsProcessor.computeSingleItemReuse(reuse, itemsResult, transaction.getFinal().createStack());
                if (!itemsResult.isEmpty()) {
                    Iterator<ItemStack> reuseResultIterator = itemsResult.iterator();
                    transaction.setCustom(reuseResultIterator.next().createSnapshot());
                    reuseResultIterator.forEachRemaining(stack -> transactionsIterator.add(new Transaction<>(transaction.getFinal(), stack.createSnapshot())));
                }
            }
        }
    }
}
