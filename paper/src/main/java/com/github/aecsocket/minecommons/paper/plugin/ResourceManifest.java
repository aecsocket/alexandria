package com.github.aecsocket.minecommons.paper.plugin;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Set;

/**
 * A resource, defined in a plugin's JAR, that sets up resource loading options.
 */
@ConfigSerializable
/* package */ record ResourceManifest(
    String settings,
    I18N i18n,
    Set<String> saved
) {
    /**
     * Defines language loading options.
     */
    @ConfigSerializable
    /* package */ record I18N(
        String root,
        Set<String> paths
    ) {}
}
