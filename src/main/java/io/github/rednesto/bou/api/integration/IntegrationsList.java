package io.github.rednesto.bou.api.integration;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.Plugin;

import java.util.*;

import javax.annotation.Nullable;

public class IntegrationsList<I extends Integration> {

    private final Logger logger;

    private final String name;

    private final Map<String, I> integrationsById = new HashMap<>();
    private final Map<String, I> integrationsByShortId = new HashMap<>();
    private final Set<String> conflictingShortIds = new HashSet<>();

    public IntegrationsList(String name) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(name);
    }

    /**
     * Registers the given integration.
     * <p>
     * Registration can fail if the ID is incorrect or incomplete,
     * or if an integration with this ID has already been registered.
     * <p>
     * This method will not request a short ID mapping (see {@link #register(Integration, boolean)})
     *
     * @param integration the integration to register
     *
     * @return true if registration was successful, false otherwise
     */
    public boolean register(I integration) {
        return register(integration, false);
    }

    /**
     * Registers the given integration.
     * <p>
     * Registration can fail if the ID is incorrect or incomplete,
     * or if an integration with this ID has already been registered.
     * <p>
     * Note that short ID mapping assignation failures will not mark the registration as failed.
     *
     * @param integration the integration to register
     * @param registerShortId whether we should also register a short ID mapping for this integration.
     *         Be aware that if another integration has the same short ID none of them will have a short ID mapping.
     *
     * @return true if registration was successful, false otherwise
     */
    public boolean register(I integration, boolean registerShortId) {
        String integrationId = integration.getId().toLowerCase(Locale.ROOT);
        int separatorIndex = integrationId.indexOf(":");
        if (separatorIndex == -1) {
            logger.error("Something tried to register an integration whose ID ('{}') is missing a namespace, ignoring it.", integrationId);
            return false;
        }

        String namespace = integrationId.substring(0, separatorIndex);
        if (!Plugin.ID_PATTERN.matcher(namespace).matches()) {
            logger.error("Invalid integration ID namespace '{}', ignoring it.", namespace);
            return false;
        }

        String shortId = integrationId.substring(separatorIndex + 1);
        if (!Plugin.ID_PATTERN.matcher(shortId).matches()) {
            logger.error("Invalid integration short ID '{}', ignoring it.", shortId);
            return false;
        }

        if (integrationsById.containsKey(integrationId)) {
            logger.error("An integration with the ID '{}' has already been registered, skipping this registration.", integrationId);
            return false;
        }

        integrationsById.put(integrationId, integration);

        if (!registerShortId) {
            return true;
        }

        if (conflictingShortIds.contains(shortId)) {
            logger.warn("Integration '{}' wanted the short ID mapping '{}' which already had conflicts, skipping short ID mapping.", integrationId, shortId);
            return true;
        }

        I presentShortIdMapping = integrationsByShortId.get(shortId);
        if (presentShortIdMapping != null) {
            logger.warn("An integration with the short ID '{}' has been already registered ('{}'), was registering '{}'. Mapping for this short ID is now disabled.",
                    shortId, presentShortIdMapping.getId(), integrationId);

            integrationsByShortId.remove(shortId);
            conflictingShortIds.add(shortId);
            return true;
        }

        integrationsByShortId.put(shortId, integration);
        return true;
    }

    @Nullable
    public I getById(String id) {
        return integrationsById.get(id.toLowerCase(Locale.ROOT));
    }

    @Nullable
    public I getById(String id, boolean canBeShortId) {
        if (id.contains(":")) {
            return getById(id);
        }

        if (canBeShortId) {
            return getByShortId(id);
        }

        return null;
    }

    @Nullable
    public I getByShortId(String shortId) {
        return integrationsByShortId.get(shortId.toLowerCase(Locale.ROOT));
    }

    public Collection<I> getAll() {
        return ImmutableSet.copyOf(this.integrationsById.values());
    }

    public final String getName() {
        return name;
    }
}
