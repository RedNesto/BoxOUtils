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
package io.github.rednesto.bou.api;

import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.event.EventContextKey;

public class BouEventContextKeys {

    public static final EventContextKey<CustomLootProcessingContext> CUSTOM_LOOT_PROCESSING_CONTEXT = EventContextKey.builder()
            .type(CustomLootProcessingContext.class)
            .key(ResourceKey.of("box-o-utils", "custom_loot_processing_context"))
            .build();

    public static final EventContextKey<Boolean> IS_FAST_HARVESTING = EventContextKey.builder()
            .type(Boolean.class)
            .key(ResourceKey.of("box-o-utils", "is_fast_harvesting"))
            .build();

    public static final EventContextKey<Boolean> IS_REUSE_DROPS = EventContextKey.builder()
            .type(Boolean.class)
            .key(ResourceKey.of("box-o-utils", "is_reuse_drops"))
            .build();
}
