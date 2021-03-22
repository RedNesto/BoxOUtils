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

import io.github.rednesto.bou.api.customdrops.CustomDropsProviderFactory;
import io.github.rednesto.bou.api.customdrops.CustomDropsProviderFactoryIntegrations;
import io.github.rednesto.bou.api.requirement.RequirementProvider;
import io.github.rednesto.bou.api.requirement.RequirementProviderIntegrations;
import org.spongepowered.api.Sponge;

import java.util.function.Consumer;

public final class BouUtils {

    public static boolean isTesting() {
        return Boolean.getBoolean("bou.is_testing");
    }

    public static boolean isNoSponge() {
        return Boolean.getBoolean("bou.no_sponge");
    }

    public static boolean isOnlyLinting() {
        return Boolean.getBoolean("bou.lint_only");
    }

    public static void registerIntegrations(IntegrationsManager integrationsManager, boolean forceLoad) {
        CustomDropsProviderFactoryIntegrations customDropsProviders = integrationsManager.getCustomDropsProviderFactoryIntegrations();
        Consumer<CustomDropsProviderFactory> customDropsProviderRegistration = provider -> customDropsProviders.register(provider, true);
        if (forceLoad || Sponge.getPluginManager().isLoaded("file-inventories")) {
            reflectiveRegistration("io.github.rednesto.bou.integration.fileinventories.FileInventoriesCustomDropsProvider$Factory",
                    customDropsProviderRegistration, "FileInventoriesCustomDropsProvider.Factory", "FileInventories");
        }

        if (forceLoad || Sponge.getPluginManager().isLoaded("byte-items")) {
            reflectiveRegistration("io.github.rednesto.bou.integration.byteitems.ByteItemsCustomDropsProvider$Factory",
                    customDropsProviderRegistration, "ByteItemsCustomDropsProvider.Factory", "ByteItems");
        }

        RequirementProviderIntegrations requirementsProviders = integrationsManager.getRequirementsProviderIntegrations();
        Consumer<RequirementProvider> requirementsProviderRegistration = provider -> requirementsProviders.register(provider, true);
        if (forceLoad || Sponge.getPluginManager().isLoaded("griefdefender")) {
            reflectiveRegistration("io.github.rednesto.bou.integration.griefdefender.GriefDefenderRegionRequirement$Provider",
                    requirementsProviderRegistration, "GriefDefenderRegionRequirement.Provider", "GriefDefender");
        }

        if (forceLoad || Sponge.getPluginManager().isLoaded("griefprevention")) {
            reflectiveRegistration("io.github.rednesto.bou.integration.griefprevention.GriefPreventionRegionRequirement$Provider",
                    requirementsProviderRegistration, "GriefPreventionRegionRequirement.Provider", "GriefPrevention");
        }

        if (forceLoad || Sponge.getPluginManager().isLoaded("universeguard")) {
            reflectiveRegistration("io.github.rednesto.bou.integration.universeguard.UniverseGuardRegionRequirement$Provider",
                    requirementsProviderRegistration, "UniverseGuardRegionRequirement.Provider", "UniverseGuard", true);
        }
    }

    // Integrations are not on the plugin's classpath, so we use reflection to get them
    private static <T> void reflectiveRegistration(String className, Consumer<T> registration, String friendlyClassName, String integrationName) {
        reflectiveRegistration(className, registration, friendlyClassName, integrationName, false);
    }

    // Integrations are not on the plugin's classpath, so we use reflection to get them
    private static <T> void reflectiveRegistration(String className, Consumer<T> registration, String friendlyClassName, String integrationName, boolean ignoreCNF) {
        try {
            //noinspection unchecked
            Class<T> clazz = (Class<T>) Class.forName(className);
            registration.accept(clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            BoxOUtils.getInstance().getLogger().error("Could not instantiate {}", friendlyClassName, e);
        } catch (ClassNotFoundException e) {
            if (!ignoreCNF) {
                BoxOUtils.getInstance().getLogger().warn("{} is loaded but its integration could not be found", integrationName, e);
            }
        }
    }

    private BouUtils() {}
}
