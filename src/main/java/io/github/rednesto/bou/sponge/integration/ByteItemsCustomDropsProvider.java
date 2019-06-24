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
package io.github.rednesto.bou.sponge.integration;

import de.randombyte.byteitems.api.ByteItemsService;
import io.github.rednesto.bou.sponge.BoxOUtils;
import io.github.rednesto.bou.sponge.CustomDropsProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import javax.annotation.Nullable;

public class ByteItemsCustomDropsProvider implements CustomDropsProvider {

    @Nullable
    private ByteItemsService backingService;

    @Override
    public void init(BoxOUtils plugin) {
        Sponge.getServiceManager().provide(ByteItemsService.class).ifPresent(service -> backingService = service);
        Sponge.getEventManager().registerListeners(plugin, this);
    }

    @Override
    public String getId() {
        return "byte-items";
    }

    @Override
    public Optional<ItemStack> createItemStack(String id, @Nullable Player targetPlayer) {
        if (backingService == null)
            return Optional.empty();

        return backingService.get(id).map(ItemStackSnapshot::createStack);
    }

    @Listener
    public void onServiceProviderChange(ChangeServiceProviderEvent event) {
        if (event.getService().equals(ByteItemsService.class))
            backingService = (ByteItemsService) event.getNewProvider();
    }
}
