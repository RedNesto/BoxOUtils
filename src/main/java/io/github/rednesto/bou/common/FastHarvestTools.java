package io.github.rednesto.bou.common;

import com.google.common.base.MoreObjects;

import java.util.List;

public class FastHarvestTools {

    private boolean enabled;
    private boolean isWhitelist;
    private List<String> toolsIds;

    public FastHarvestTools(boolean enabled, boolean isWhitelist, List<String> toolsIds) {
        this.enabled = enabled;
        this.isWhitelist = isWhitelist;
        this.toolsIds = toolsIds;
    }

    public boolean isEnabled() {
        return enabled;
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
                isWhitelist == that.isWhitelist &&
                toolsIds.equals(that.toolsIds);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("enabled", enabled)
                .add("isWhitelist", isWhitelist)
                .add("toolsIds", toolsIds)
                .toString();
    }
}
