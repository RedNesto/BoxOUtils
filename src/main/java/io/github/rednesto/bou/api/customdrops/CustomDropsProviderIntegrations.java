package io.github.rednesto.bou.api.customdrops;

import io.github.rednesto.bou.IntegrationsManager;
import io.github.rednesto.bou.api.integration.IntegrationsList;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

import javax.annotation.Nullable;

public class CustomDropsProviderIntegrations extends IntegrationsList<CustomDropsProvider> {

    private final CustomDropsProvider vanillaProvider;

    public CustomDropsProviderIntegrations(CustomDropsProvider vanillaProvider) {
        super("CustomDropsProvider Integrations", "box-o-utils");
        this.vanillaProvider = vanillaProvider;
        register(vanillaProvider, true);
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
