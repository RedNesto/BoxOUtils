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
package io.github.rednesto.bou;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IdSelector {

    @Nullable
    public static <T> T selectInMap(Map<String, T> available, String id) {
        String lootId = resolveId(available.keySet(), id);
        return lootId != null ? available.get(lootId) : null;
    }

    @Nullable
    public static String resolveId(Set<String> availableIds, String id) {
        if (availableIds.contains(id)) {
            return id;
        }

        String queryModId = SpongeUtils.getModId(id);
        if (queryModId == null) {
            return null;
        }

        String modIdWildcard = queryModId + ":*";
        if (availableIds.contains(modIdWildcard)) {
            return modIdWildcard;
        }

        if (availableIds.contains("*")) {
            return "*";
        }

        return null;
    }

    public static class Cache {

        private final Map<String, String> keysCache = new HashMap<>();

        @Nullable
        public <T> T get(Map<String, T> available, String id) {
            String mapKey = keysCache.get(id);
            if (mapKey == null) {
                if (keysCache.containsKey(id)) {
                    return null;
                }
                mapKey = IdSelector.resolveId(available.keySet(), id);
                keysCache.put(id, mapKey);
            }
            return available.get(mapKey);
        }

        public void clear() {
            keysCache.clear();
        }
    }
}
