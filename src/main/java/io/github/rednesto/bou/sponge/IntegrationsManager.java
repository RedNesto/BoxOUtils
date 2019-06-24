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
package io.github.rednesto.bou.sponge;

import io.github.rednesto.bou.common.Config;
import io.github.rednesto.bou.common.requirement.RequirementProvider;
import io.github.rednesto.bou.sponge.integration.ByteItemsCustomDropsProvider;
import io.github.rednesto.bou.sponge.integration.FileInventoriesCustomDropsProvider;
import io.github.rednesto.bou.sponge.integration.requirements.GriefPreventionRegionRequirement;
import io.github.rednesto.bou.sponge.integration.vanilla.VanillaCustomDropsProvider;
import io.github.rednesto.bou.sponge.requirements.DataByKeyRequirementProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public final class IntegrationsManager {

    public static final IntegrationsManager INSTANCE = new IntegrationsManager();

    private boolean customDropsProvidersInit = false;
    private final CustomDropsProvider defaultCustomDropsProvider = new VanillaCustomDropsProvider();
    private final Map<String, CustomDropsProvider> customDropsProviders = new HashMap<>();

    private final Map<String, RequirementProvider> requirementProviders = new HashMap<>();

    private IntegrationsManager() {
        register(defaultCustomDropsProvider);
        register(new DataByKeyRequirementProvider<>("block_data", BlockSnapshot.class));
        register(new DataByKeyRequirementProvider<>("entity_data", EntitySnapshot.class));
    }

    public void register(CustomDropsProvider customDropsProvider) {
        customDropsProviders.put(customDropsProvider.getId(), customDropsProvider);
        if (customDropsProvidersInit)
            customDropsProvider.init(BoxOUtils.getInstance());
    }

    public void register(RequirementProvider requirementProvider) {
        requirementProviders.put(requirementProvider.getId(), requirementProvider);
    }

    public CustomDropsProvider getDefaultCustomDropsProvider() {
        return defaultCustomDropsProvider;
    }

    public Optional<ItemStack> createCustomDropStack(@Nullable String providerId, String id, @Nullable Player targetPlayer) {
        if (providerId == null)
            return defaultCustomDropsProvider.createItemStack(id, targetPlayer);

        CustomDropsProvider provider = customDropsProviders.get(providerId);
        if (provider != null)
            return provider.createItemStack(id, targetPlayer);

        return Optional.empty();
    }

    @Nullable
    public RequirementProvider getRequirementProvider(String id) {
        return requirementProviders.get(id);
    }

    void loadBuiltins() {
        if (Sponge.getPluginManager().isLoaded("file-inventories")) {
            register(new FileInventoriesCustomDropsProvider());
        }

        if (Sponge.getPluginManager().isLoaded("byte-items")) {
            register(new ByteItemsCustomDropsProvider());
        }

        if (Sponge.getPluginManager().isLoaded("griefprevention")) {
            register(new GriefPreventionRegionRequirement.Provider());
        }

        if (Sponge.getPluginManager().isLoaded("universeguard")) {
            try {
                // This integration cannot be on the plugin's classpath, so we must use reflection to get it
                Class<?> universeguardProvider = Class.forName("io.github.rednesto.bou.sponge.integration.universeguard.UniverseGuardRegionRequirement$Provider");
                register((RequirementProvider) universeguardProvider.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                BoxOUtils.getInstance().getLogger().error("Could not instantiate UniverseGuardRegionRequirement.Provider", e);
            } catch (ClassNotFoundException e) {
                BoxOUtils.getInstance().getLogger().info("UniverseGuard is loaded but its integration could not be found");
            }
        }
    }

    void initIntegrations(BoxOUtils plugin) {
        if (Config.CUSTOM_BLOCKS_DROPS_ENABLED || Config.CUSTOM_MOBS_DROPS_ENABLED)
            initCustomDropsProviders(plugin);
    }

    private void initCustomDropsProviders(BoxOUtils plugin) {
        if (customDropsProvidersInit)
            return;

        customDropsProvidersInit = true;
        defaultCustomDropsProvider.init(plugin);
        for (CustomDropsProvider provider : customDropsProviders.values()) {
            provider.init(plugin);
        }
    }
}
