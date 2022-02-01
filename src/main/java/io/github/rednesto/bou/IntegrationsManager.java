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

import io.github.rednesto.bou.api.customdrops.CustomDropsProviderFactoryIntegrations;
import io.github.rednesto.bou.api.customdrops.CustomLootComponentProviderIntegrations;
import io.github.rednesto.bou.api.customdrops.CustomLootProcessingContext;
import io.github.rednesto.bou.api.customdrops.CustomLootRecipientProviderIntegrations;
import io.github.rednesto.bou.api.requirement.RequirementProviderIntegrations;
import io.github.rednesto.bou.integration.customdrops.CommandLootComponent;
import io.github.rednesto.bou.integration.customdrops.ExperienceLootComponent;
import io.github.rednesto.bou.integration.customdrops.MoneyLootComponent;
import io.github.rednesto.bou.integration.customdrops.recipients.ContextLocationLootRecipient;
import io.github.rednesto.bou.integration.customdrops.recipients.PlayerInventoryLootRecipient;
import io.github.rednesto.bou.integration.vanilla.VanillaCustomDropsProvider;
import io.github.rednesto.bou.requirements.*;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.function.Function;

public final class IntegrationsManager {

    private boolean vanillaBuiltinsLoaded = false;

    private final CustomDropsProviderFactoryIntegrations customDropsProviderFactoryIntegrations = new CustomDropsProviderFactoryIntegrations();
    private final RequirementProviderIntegrations requirementProviderIntegrations = new RequirementProviderIntegrations();
    private final CustomLootComponentProviderIntegrations customLootComponentProviderIntegrations = new CustomLootComponentProviderIntegrations();
    private final CustomLootRecipientProviderIntegrations customLootRecipientProviderIntegrations = new CustomLootRecipientProviderIntegrations();

    public void loadVanillaBuiltins() {
        if (vanillaBuiltinsLoaded) {
            return;
        }

        vanillaBuiltinsLoaded = true;

        customDropsProviderFactoryIntegrations.register(new VanillaCustomDropsProvider.Factory(), true);

        requirementProviderIntegrations.register(new BiomesRequirement.Provider(), true);
        requirementProviderIntegrations.register(new DataByKeyRequirementProvider<>("box-o-utils:block_data", BlockSnapshot.class), true);
        requirementProviderIntegrations.register(new DataByKeyRequirementProvider<>("box-o-utils:entity_data", EntitySnapshot.class), true);
        Function<CustomLootProcessingContext, Object> usedItemContainerSelector = context -> context.getCause().context().require(EventContextKeys.USED_ITEM);
        requirementProviderIntegrations.register(new DataByKeyRequirementProvider<>("box-o-utils:used_item_data", ItemStackSnapshot.class, usedItemContainerSelector), true);
        requirementProviderIntegrations.register(new EnchantmentsRequirement.Provider(), true);
        requirementProviderIntegrations.register(new CaughtFishRequirement.Provider(), true);
        requirementProviderIntegrations.register(new PermissionsRequirement.Provider(), true);
        requirementProviderIntegrations.register(new WorldsRequirement.Provider(), true);

        customLootComponentProviderIntegrations.register(new CommandLootComponent.Provider(), true);
        customLootComponentProviderIntegrations.register(new ExperienceLootComponent.Provider(), true);
        customLootComponentProviderIntegrations.register(new MoneyLootComponent.Provider(), true);

        customLootRecipientProviderIntegrations.register(new ContextLocationLootRecipient.Provider(), true);
        customLootRecipientProviderIntegrations.register(new PlayerInventoryLootRecipient.Provider(), true);
    }

    public void initIntegrations(BoxOUtils plugin) {
        customDropsProviderFactoryIntegrations.initIntegrations(plugin);
        requirementProviderIntegrations.initIntegrations(plugin);
        customLootComponentProviderIntegrations.initIntegrations(plugin);
        customLootRecipientProviderIntegrations.initIntegrations(plugin);
    }

    public void reloadIntegrations(BoxOUtils plugin) {
        customDropsProviderFactoryIntegrations.reloadIntegrations(plugin);
        requirementProviderIntegrations.reloadIntegrations(plugin);
        customLootComponentProviderIntegrations.reloadIntegrations(plugin);
        customLootRecipientProviderIntegrations.reloadIntegrations(plugin);
    }

    public CustomDropsProviderFactoryIntegrations getCustomDropsProviderFactoryIntegrations() {
        return customDropsProviderFactoryIntegrations;
    }

    public RequirementProviderIntegrations getRequirementsProviderIntegrations() {
        return requirementProviderIntegrations;
    }

    public CustomLootComponentProviderIntegrations getCustomLootComponentProviderIntegrations() {
        return customLootComponentProviderIntegrations;
    }

    public CustomLootRecipientProviderIntegrations getCustomLootRecipientProviderIntegrations() {
        return customLootRecipientProviderIntegrations;
    }

    public static IntegrationsManager getInstance() {
        return BoxOUtils.getInstance().getIntegrationsManager();
    }
}
