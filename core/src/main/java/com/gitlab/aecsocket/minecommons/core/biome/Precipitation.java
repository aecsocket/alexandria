package com.gitlab.aecsocket.minecommons.core.biome;

/**
 * The types of precipitation available in a biome.
 */
public enum Precipitation {
    /** No precipitation occurs. */
    NONE    ("none"),
    /** Rain falls. */
    RAIN    ("rain"),
    /** Snow falls. */
    SNOW    ("snow");

    private final String key;

    Precipitation(String key) {
        this.key = key;
    }

    /**
     * The key of this value.
     * @return The key.
     */
    public String key() { return key; }
}
