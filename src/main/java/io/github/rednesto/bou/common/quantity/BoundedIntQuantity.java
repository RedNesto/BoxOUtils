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
package io.github.rednesto.bou.common.quantity;

import com.google.common.base.MoreObjects;

import java.util.Random;

public class BoundedIntQuantity implements IIntQuantity {

    private int from;
    private int to;

    private static final Random RANDOM = new Random();

    public BoundedIntQuantity(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int get() {
        if(from == to)
            return to;

        return RANDOM.nextInt(to + 1 - from) + from;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public static BoundedIntQuantity parse(String toParse) {
        String[] bounds = toParse.split("-", 2);
        int from = Integer.parseInt(bounds[0]);
        int to = Integer.parseInt(bounds[1]);
        return new BoundedIntQuantity(from, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoundedIntQuantity)) {
            return false;
        }

        BoundedIntQuantity that = (BoundedIntQuantity) o;
        return from == that.from &&
                to == that.to;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("from", from)
                .add("to", to)
                .toString();
    }
}
