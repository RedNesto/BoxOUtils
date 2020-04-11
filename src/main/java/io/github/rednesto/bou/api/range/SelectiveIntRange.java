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
package io.github.rednesto.bou.api.range;

import com.google.common.base.MoreObjects;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SelectiveIntRange implements IntRange {

    private final List<Integer> values;

    public SelectiveIntRange(List<Integer> values) {
        this.values = values;
    }

    public SelectiveIntRange(Integer... values) {
        this.values = Arrays.asList(values);
    }

    @Override
    public boolean isInRange(int value) {
        return this.values.contains(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SelectiveIntRange)) {
            return false;
        }

        SelectiveIntRange that = (SelectiveIntRange) o;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("values", values)
                .toString();
    }

    public static SelectiveIntRange parse(String value) {
        try {
            List<Integer> values = Arrays.stream(value.split(";"))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            return new SelectiveIntRange(values);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
