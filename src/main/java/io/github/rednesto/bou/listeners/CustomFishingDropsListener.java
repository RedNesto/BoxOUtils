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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.rednesto.bou.Config;
import io.github.rednesto.bou.CustomDropsProcessor;
import io.github.rednesto.bou.SpongeUtils;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CustomFishingDropsListener {

    private final Cache<UUID, List<CustomLoot>> requirementResultsTracker = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.SECONDS)
        .build();

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

        Player player = event.getCause().first(Player.class).orElse(null);
        if (player != null) {
            requirementResultsTracker.put(player.getUniqueId(), loots);
        }

        boolean overwritten = processOverwrite(transactions, loots);
        if (!overwritten) {
            processReuse(transactions, loots);
        }

        Location<World> location = event.getFishHook().getLocation();
        Location<World> experienceSpawnLocation = SpongeUtils.center(player != null ? player.getLocation() : location);
        CustomLootProcessingContext context = new CustomLootProcessingContext(loots, event, event.getFishHook(), event.getCause(), player, location, experienceSpawnLocation);
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

    @Listener
    public void onExpOrbSpawn(SpawnEntityEvent event, @First Player player) {
        if (!Config.getFishingDrops().enabled) {
            return;
        }

        List<CustomLoot> loots = requirementResultsTracker.getIfPresent(player.getUniqueId());
        if (loots != null) {
            for (CustomLoot loot : loots) {
                if (loot.isExpOverwrite()) {
                    event.filterEntities(entity -> !(entity instanceof ExperienceOrb));
                    break;
                }
            }
        }
    }
}
