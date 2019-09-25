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
package io.github.rednesto.bou.tests.framework.mock

import org.spongepowered.api.item.enchantment.EnchantmentType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.translation.Translation

class MockEnchantmentType(private val id: String) : EnchantmentType {
    override fun getMinimumLevel(): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun getTranslation(): Translation = throw NotImplementedError("MockPlayer method not implemented")

    override fun canBeAppliedToStack(stack: ItemStack): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getMinimumEnchantabilityForLevel(level: Int): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun getName(): String = throw NotImplementedError("MockPlayer method not implemented")

    override fun getId(): String = id

    override fun isCompatibleWith(enchantmentType: EnchantmentType): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun isTreasure(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun getMaximumEnchantabilityForLevel(level: Int): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun getMaximumLevel(): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun getWeight(): Int = throw NotImplementedError("MockPlayer method not implemented")

    override fun isCurse(): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    override fun canBeAppliedByTable(stack: ItemStack): Boolean = throw NotImplementedError("MockPlayer method not implemented")

    companion object {
        val SILK_TOUCH = MockEnchantmentType("minecraft:silk_touch")
        val EFFICIENCY = MockEnchantmentType("minecraft:efficiency")
        val UNBREAKING = MockEnchantmentType("minecraft:unbreaking")
        val FORTUNE = MockEnchantmentType("minecraft:fortune")
    }
}
