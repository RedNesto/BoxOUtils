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
package io.github.rednesto.bou.requirements;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.api.requirement.AbstractRequirement;
import io.github.rednesto.bou.api.requirement.Requirement;
import io.github.rednesto.bou.api.requirement.RequirementConfigurationException;
import io.github.rednesto.bou.api.requirement.RequirementProvider;
import io.github.rednesto.bou.api.utils.EnchantmentsFilter;
import io.github.rednesto.bou.config.serializers.BouTypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.List;

public class EnchantmentsRequirement extends AbstractRequirement<Object> {

    private final EnchantmentsFilter enchantmentsFilter;

    public EnchantmentsRequirement(EnchantmentsFilter enchantmentsFilter) {
        super("enchantments", Object.class);
        this.enchantmentsFilter = enchantmentsFilter;
    }

    @Override
    public boolean fulfills(Object source, Cause cause) {
        ItemStackSnapshot usedItem = cause.getContext().get(EventContextKeys.USED_ITEM).orElse(null);
        if (usedItem == null) {
            return true;
        }

        List<Enchantment> itemEnchantments = usedItem.get(Keys.ITEM_ENCHANTMENTS).orElse(null);
        if (itemEnchantments == null) {
            return true;
        }

        return enchantmentsFilter.test(itemEnchantments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnchantmentsRequirement)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        EnchantmentsRequirement that = (EnchantmentsRequirement) o;
        return enchantmentsFilter.equals(that.enchantmentsFilter);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("enchantmentsFilter", enchantmentsFilter)
                .toString();
    }

    public static class Provider implements RequirementProvider {

        @Override
        public String getId() {
            return "box-o-utils:enchantments";
        }

        @Override
        public Requirement<?> provide(ConfigurationNode node) throws RequirementConfigurationException {
            try {
                EnchantmentsFilter filter = node.getValue(BouTypeTokens.ENCHANTMENTS_FILTER);
                if (filter != null) {
                    return new EnchantmentsRequirement(filter);
                }

                throw new RequirementConfigurationException("Enchantment requirement is empty.");
            } catch (ObjectMappingException e) {
                throw new RequirementConfigurationException(e);
            }
        }
    }
}
