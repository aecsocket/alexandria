package com.gitlab.aecsocket.minecommons.paper.plugin;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Set;

/**
 * A resource, defined in a plugin's JAR, that sets up resource loading options.
 */
@ConfigSerializable
/* package */ record ResourceManifest(
        String settings,
        Language language,
        Set<String> saved
) {
    /**
     * Defines language loading options.
     */
    @ConfigSerializable
    /* package */ record Language(
            String dataPath,
            String formats,
            Set<String> translations
    ) {}
}
