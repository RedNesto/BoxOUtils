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
package io.github.rednesto.bou.api.integration;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import io.github.rednesto.bou.BouUtils;
import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.SpongeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.Plugin;

import java.util.*;

import javax.annotation.Nullable;

public class IntegrationsList<I extends Integration> {

    private final Logger logger;

    private final String name;
    @Nullable
    private final String defaultNamespace;

    private final Map<String, I> integrationsById = new HashMap<>();
    private final Map<String, I> integrationsByShortId = new HashMap<>();
    private final Set<String> conflictingShortIds = new HashSet<>();

    private boolean initDone;

    public IntegrationsList(String name) {
        this(name, null);
    }

    public IntegrationsList(String name, @Nullable String defaultNamespace) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(name);
        this.defaultNamespace = defaultNamespace;
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

        if (this.defaultNamespace != null) {
            String idWithDefaultNamespace = SpongeUtils.addNamespaceIfNeeded(shortId, this.defaultNamespace);
            if (integrationsById.containsKey(idWithDefaultNamespace)) {
                logger.warn("Integration '{}' has the same short ID than an existing integration on the default namespace '{}'." +
                        " This may cause ambiguity when using the short ID form, the default namespace will take precedence.",
                        integrationId, this.defaultNamespace);
            }
        }

        BoxOUtils plugin = BoxOUtils.getInstance();
        if (initDone && shouldInitIntegrationOnRegistration()) {
            initAndLoadIntegration(integration, plugin, true);
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

    public void initIntegrations(BoxOUtils plugin) {
        if (initDone) {
            return;
        }

        initDone = true;
        integrationsById.values().forEach(integration -> initAndLoadIntegration(integration, plugin, true));
    }

    public void reloadIntegrations(BoxOUtils plugin) {
        integrationsById.values().forEach(integration -> initAndLoadIntegration(integration, plugin, false));
    }

    private void initAndLoadIntegration(I integration, BoxOUtils plugin, boolean doInit) {
        if (doInit) {
            try {
                integration.init(plugin);
            } catch (Throwable e) {
                this.logger.error("An error occurred when initializing the integration {}.", integration.getId(), e);
                return;
            }
        }

        try {
            integration.load(plugin);
        } catch (Throwable e) {
            this.logger.error("An error occurred when loading the integration {}.", integration.getId(), e);
        }
    }

    /**
     * This method is only meant to be used for unit tests.
     * It should always return {@code true} in a production environment.
     */
    @VisibleForTesting
    protected boolean shouldInitIntegrationOnRegistration() {
        return !BouUtils.isTesting();
    }

    @Nullable
    public I getById(String id) {
        String lowercaseId = id.toLowerCase(Locale.ROOT);
        if (!lowercaseId.contains(":")) {
            if (this.defaultNamespace == null) {
                // Don't try to get an integration without namespace
                return null;
            }

            lowercaseId = SpongeUtils.addNamespaceIfNeeded(lowercaseId, this.defaultNamespace);
        }

        return integrationsById.get(lowercaseId);
    }

    @Nullable
    public I getById(String id, boolean canBeShortId) {
        I integrationById = getById(id);
        if (integrationById != null) {
            return integrationById;
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

    @Nullable
    public final String getDefaultNamespace() {
        return defaultNamespace;
    }

    public final String getName() {
        return name;
    }
}
