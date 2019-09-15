package io.github.rednesto.bou.api.integration;

import io.github.rednesto.bou.BoxOUtils;

public interface Integration {

    String getId();

    /**
     * Called only once. Used for one-time actions like getting a service instance, or logging stuff.
     *
     * @param plugin the instance of the main plugin class
     */
    default void init(BoxOUtils plugin) { }

    /**
     * Called right after {@link #init(BoxOUtils)} and each time Box O' Utils configuration is reloaded,
     * usually via the Sponge reload mechanism or {@code /bou reload}.
     *
     * @param plugin the instance of the main plugin class
     */
    default void load(BoxOUtils plugin) { }
}
