package io.github.rednesto.bou.common;

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
}
