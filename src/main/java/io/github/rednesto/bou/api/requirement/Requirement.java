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
package io.github.rednesto.bou.api.requirement;

import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;

public interface Requirement {

    String getId();

    /**
     * Indicates whether this requirement can be used for the given source.
     *
     * @param context the context in which the current loot is processed
     *
     * @return {@code true} if this requirement can be applied to the source, {@code false} otherwise
     */
    default boolean appliesTo(CustomLootProcessingContext context) {
        return true;
    }

    /**
     * Indicates if the given source fulfills this requirement.
     *
     * @param context the context in which the current loot is processed
     *
     * @return {@code true} if the source fulfills this requirement, {@code false} otherwise
     */
    boolean fulfills(CustomLootProcessingContext context);
}
