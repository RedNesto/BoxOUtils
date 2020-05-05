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
package io.github.rednesto.bou.api.customdrops;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import io.github.rednesto.bou.api.quantity.BoundedIntQuantity;
import io.github.rednesto.bou.api.quantity.IntQuantity;
import io.github.rednesto.bou.config.serializers.BouTypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nullable;

public abstract class BasicCustomDropsProvider implements CustomDropsProvider {

    private final String itemId;
    @Nullable
    private final String displayname;
    private final double chance;
    @Nullable
    private final IntQuantity quantity;

    protected BasicCustomDropsProvider(String itemId, @Nullable String displayname, double chance, @Nullable IntQuantity quantity) {
        this.itemId = itemId;
        this.displayname = displayname;
        this.chance = chance / 100;
        this.quantity = quantity;
    }

    @Override
    public final Collection<ItemStack> createStacks(CustomLootProcessingContext context) {
        if (!shouldLoot()) {
            return Collections.emptyList();
        }

        ItemStack baseStack = createStack(context, itemId);
        if (baseStack == null) {
            return Collections.emptyList();
        }

        if (quantity != null) {
            baseStack.setQuantity(quantity.get());
        }

        if (displayname != null) {
            baseStack.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(displayname));
        }

        ItemStack stack = Preconditions.checkNotNull(transform(context, baseStack), "#transform returned null");
        return Collections.singletonList(stack);
    }

    public final boolean shouldLoot() {
        return chance <= 0 || Math.random() <= chance;
    }

    @Nullable
    protected ItemStack createStack(CustomLootProcessingContext context, String itemId) {
        return Sponge.getRegistry().getType(ItemType.class, itemId)
                .map(ItemStack::of)
                .orElse(null);
    }

    protected ItemStack transform(CustomLootProcessingContext context, ItemStack stack) {
        return stack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicCustomDropsProvider that = (BasicCustomDropsProvider) o;
        return Double.compare(that.chance, chance) == 0 &&
                itemId.equals(that.itemId) &&
                Objects.equals(displayname, that.displayname) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, displayname, chance, quantity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("itemId", itemId)
                .add("displayname", displayname)
                .add("chance", chance)
                .add("quantity", quantity)
                .toString();
    }

    public static abstract class BasicFactory implements CustomDropsProviderFactory {

        @Override
        public final CustomDropsProvider provide(ConfigurationNode node) throws ProviderConfigurationException {
            String itemId;
            if (node.getValueType() == ValueType.SCALAR) {
                itemId = node.getString();
            } else if (node.hasMapChildren()) {
                itemId = node.getNode("type").getString();
            } else {
                throw new ProviderConfigurationException("Provided value should be scalar or an object.");
            }

            if (itemId == null) {
                throw new ProviderConfigurationException("Type is missing.");
            }

            IntQuantity quantity;
            try {
                quantity = node.getNode("quantity").getValue(BouTypeTokens.INT_QUANTITY, (IntQuantity) null);
            } catch (ObjectMappingException e) {
                throw new ProviderConfigurationException("Invalid IntQuantity", e);
            }

            if (quantity instanceof BoundedIntQuantity) {
                BoundedIntQuantity boundedQuantity = (BoundedIntQuantity) quantity;
                if (boundedQuantity.getFrom() < 0) {
                    String errorMessage = String.format("The quantity lower bound (%s) is negative.",
                            boundedQuantity.getFrom());
                    throw new ProviderConfigurationException(errorMessage);
                }

                if (boundedQuantity.getTo() < boundedQuantity.getFrom()) {
                    String errorMessage = String.format("The quantity upper bound (%s) is less than its lower bound (%s).",
                            boundedQuantity.getTo(), boundedQuantity.getFrom());
                    throw new ProviderConfigurationException(errorMessage);
                }
            }

            double chance = 0;
            ConfigurationNode chanceNode = node.getNode("chance");
            if (!chanceNode.isVirtual()) {
                chance = chanceNode.getDouble(Double.NaN);
                if (Double.isNaN(chance)) {
                    String errorMessage = String.format("Chance value is not a valid number (%s).", chanceNode.getValue());
                    throw new ProviderConfigurationException(errorMessage);
                }
            }

            return provide(node, itemId, node.getNode("displayname").getString(), chance, quantity);
        }

        protected abstract BasicCustomDropsProvider provide(@Nullable ConfigurationNode node,
                                                            String itemId,
                                                            @Nullable String displayname,
                                                            double chance,
                                                            @Nullable IntQuantity quantity);
    }
}
