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

import io.github.rednesto.bou.api.customdrops.CustomDropsProviderIntegrations;
import io.github.rednesto.bou.api.requirement.RequirementProviderIntegrations;
import io.github.rednesto.bou.integration.vanilla.VanillaCustomDropsProvider;
import io.github.rednesto.bou.requirements.DataByKeyRequirementProvider;
import io.github.rednesto.bou.requirements.PermissionsRequirement;
import io.github.rednesto.bou.requirements.WorldsRequirement;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.EntitySnapshot;

public final class IntegrationsManager {

    private boolean vanillaBuiltinsLoaded = false;

    private CustomDropsProviderIntegrations customDropsProviderIntegrations = new CustomDropsProviderIntegrations(new VanillaCustomDropsProvider());
    private RequirementProviderIntegrations requirementProviderIntegrations = new RequirementProviderIntegrations();

    public void loadVanillaBuiltins() {
        if (vanillaBuiltinsLoaded) {
            return;
        }

        vanillaBuiltinsLoaded = true;

        requirementProviderIntegrations.register(new DataByKeyRequirementProvider<>("box-o-utils:block_data", BlockSnapshot.class), true);
        requirementProviderIntegrations.register(new DataByKeyRequirementProvider<>("box-o-utils:entity_data", EntitySnapshot.class), true);
        requirementProviderIntegrations.register(new PermissionsRequirement.Provider(), true);
        requirementProviderIntegrations.register(new WorldsRequirement.Provider(), true);
    }

    void initIntegrations(BoxOUtils plugin) {
        if (plugin.getBlocksDrops().enabled || plugin.getMobsDrops().enabled) {
            customDropsProviderIntegrations.initIntegrations(plugin);
        }
    }

    public static IntegrationsManager getInstance() {
        return BoxOUtils.getInstance().getIntegrationsManager();
    }

    public CustomDropsProviderIntegrations getCustomDropsProviderIntegrations() {
        return customDropsProviderIntegrations;
    }

    public RequirementProviderIntegrations getRequirementsProviderIntegrations() {
        return requirementProviderIntegrations;
    }
}
