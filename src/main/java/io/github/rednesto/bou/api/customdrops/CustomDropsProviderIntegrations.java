package io.github.rednesto.bou.api.customdrops;

import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.IntegrationsManager;
import io.github.rednesto.bou.api.integration.IntegrationsList;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

import javax.annotation.Nullable;

public class CustomDropsProviderIntegrations extends IntegrationsList<CustomDropsProvider> {

    private final CustomDropsProvider vanillaProvider;

    private boolean integrationsInitialized;

    public CustomDropsProviderIntegrations(CustomDropsProvider vanillaProvider) {
        super("CustomDropsProvider Integrations");
        this.vanillaProvider = vanillaProvider;
        register(vanillaProvider, true);
    }

    @Override
    public boolean register(CustomDropsProvider integration, boolean registerShortId) {
        boolean integrationRegistered = super.register(integration, registerShortId);
        if (integrationRegistered && integrationsInitialized) {
            integration.init(BoxOUtils.getInstance());
        }

        return integrationRegistered;
    }

    public void initIntegrations(BoxOUtils plugin) {
        if (integrationsInitialized) {
            return;
        }

        integrationsInitialized = true;
        getAll().forEach(provider -> provider.init(plugin));
    }

    public Optional<ItemStack> createCustomDropStack(@Nullable String providerId, String id, @Nullable Player targetPlayer) {
        if (providerId == null) {
            return vanillaProvider.createItemStack(id, targetPlayer);
        }

        CustomDropsProvider provider = getById(providerId, true);
        if (provider != null) {
            return provider.createItemStack(id, targetPlayer);
        }

        return Optional.empty();
    }

    public CustomDropsProvider getDefaultIntegration() {
        return this.vanillaProvider;
    }

    public static CustomDropsProviderIntegrations getInstance() {
        return IntegrationsManager.getInstance().getCustomDropsProviderIntegrations();
    }
}
