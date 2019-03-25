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
package io.github.rednesto.bou.common;

import io.github.rednesto.bou.common.quantity.IIntQuantity;

import java.util.Random;

public class MoneyLoot {

    private IIntQuantity amount;
    private String currencyId;
    private int chance;
    private String message;

    private static final Random RANDOM = new Random();

    public MoneyLoot(IIntQuantity amount, String currencyId, int chance, String message) {
        this.amount = amount;
        this.currencyId = currencyId;
        this.chance = chance;
        this.message = message;
    }

    public boolean shouldLoot() {
        return chance <= 0 || RANDOM.nextInt(100) <= chance;
    }

    public IIntQuantity getAmount() {
        return amount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public int getChance() {
        return chance;
    }

    public String getMessage() {
        return message;
    }
}
