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
package io.github.rednesto.bou.api.fastharvest;

import com.google.common.base.MoreObjects;

import java.util.List;

public class FastHarvestTools {

    private final boolean enabled;
    private final boolean damageOnUse;
    private final boolean isWhitelist;
    private final List<String> toolsIds;

    public FastHarvestTools(boolean enabled, boolean damageOnUse, boolean isWhitelist, List<String> toolsIds) {
        this.enabled = enabled;
        this.damageOnUse = damageOnUse;
        this.isWhitelist = isWhitelist;
        this.toolsIds = toolsIds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDamageOnUse() {
        return damageOnUse;
    }

    public boolean isWhitelist() {
        return isWhitelist;
    }

    public List<String> getToolsIds() {
        return toolsIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FastHarvestTools)) {
            return false;
        }

        FastHarvestTools that = (FastHarvestTools) o;
        return enabled == that.enabled &&
                damageOnUse == that.damageOnUse &&
                isWhitelist == that.isWhitelist &&
                toolsIds.equals(that.toolsIds);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("enabled", enabled)
                .add("damageOnUse", damageOnUse)
                .add("isWhitelist", isWhitelist)
                .add("toolsIds", toolsIds)
                .toString();
    }
}
