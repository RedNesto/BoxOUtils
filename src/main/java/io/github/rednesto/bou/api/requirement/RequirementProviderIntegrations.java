package io.github.rednesto.bou.api.requirement;

import io.github.rednesto.bou.IntegrationsManager;
import io.github.rednesto.bou.api.integration.IntegrationsList;

public class RequirementProviderIntegrations extends IntegrationsList<RequirementProvider> {

    public RequirementProviderIntegrations() {
        super("RequirementProvider Integrations");
    }

    public static RequirementProviderIntegrations getInstance() {
        return IntegrationsManager.getInstance().getRequirementsProviderIntegrations();
    }
}
