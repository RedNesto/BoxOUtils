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
package io.github.rednesto.bou.api.integration;

import io.github.rednesto.bou.BoxOUtils;
import org.spongepowered.api.plugin.Plugin;

/**
 * An integration is used to extend existing features, like a new CustomDrops provider to customize dropped items.
 */
public interface Integration {

    /**
     * @return the ID of this integration. It must follow the format {@code modid:integrationid}.
     *
     * @see Plugin#ID_PATTERN the pattern IDs must respect
     */
    String getId();

    /**
     * Called only once. Used for one-time actions like getting a service instance, or logging stuff.
     *
     * @param plugin the instance of the main plugin class
     */
    default void init(BoxOUtils plugin) { }

    /**
     * Called right after {@link #init(BoxOUtils)} and each time Box O' Utils configuration is reloaded,
     * usually via the Sponge reload mechanism or {@code /bou reload}.
     *
     * @param plugin the instance of the main plugin class
     */
    default void load(BoxOUtils plugin) { }
}
