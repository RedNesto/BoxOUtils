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
package io.github.rednesto.bou.api.utils;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.api.range.IntRange;
import org.spongepowered.api.item.enchantment.Enchantment;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class EnchantmentsFilter {

    private final Map<String, IntRange> neededRanges;
    private final Map<String, IntRange> disallowedRanges;
    private final Map<String, Boolean> wildcards;
    private final long positiveWildcards;

    public EnchantmentsFilter(Map<String, IntRange> neededRanges, Map<String, IntRange> disallowedRanges) {
        this(neededRanges, disallowedRanges, Collections.emptyMap());
    }

    public EnchantmentsFilter(Map<String, IntRange> neededRanges, Map<String, IntRange> disallowedRanges, Map<String, Boolean> wildcards) {
        this.neededRanges = neededRanges;
        this.disallowedRanges = disallowedRanges;
        this.wildcards = wildcards;
        this.positiveWildcards = wildcards.values().stream().filter(value -> value).count();
    }

    public boolean test(Collection<Enchantment> enchantments) {
        if (enchantments.isEmpty()) {
            return positiveWildcards == 0 && neededRanges.isEmpty();
        }

        int wildcardsEncountered = 0;
        int allowedEncountered = 0;
        for (Enchantment enchantment : enchantments) {
            String id = enchantment.getType().getId();

            Boolean wildcard = wildcards.get(id);
            if (wildcard != null) {
                if (!wildcard) {
                    return false;
                }

                wildcardsEncountered++;
                continue;
            }

            IntRange disallowedRange = disallowedRanges.get(id);
            if (disallowedRange != null) {
                if (disallowedRange.isInRange(enchantment.getLevel())) {
                    return false;
                }
            }

            IntRange allowedRange = neededRanges.get(id);
            if (allowedRange != null) {
                if (!allowedRange.isInRange(enchantment.getLevel())) {
                    return false;
                }

                allowedEncountered++;
            }
        }

        return wildcardsEncountered == positiveWildcards && allowedEncountered == neededRanges.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnchantmentsFilter)) {
            return false;
        }

        EnchantmentsFilter that = (EnchantmentsFilter) o;
        return neededRanges.equals(that.neededRanges) &&
                disallowedRanges.equals(that.disallowedRanges) &&
                wildcards.equals(that.wildcards);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("neededRanges", neededRanges)
                .add("disallowedRanges", disallowedRanges)
                .add("wildcards", wildcards)
                .toString();
    }
}
