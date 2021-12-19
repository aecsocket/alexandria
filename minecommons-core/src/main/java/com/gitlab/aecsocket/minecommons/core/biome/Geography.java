package com.gitlab.aecsocket.minecommons.core.biome;

/**
 * The types of geography/category that a biome can fall under.
 */
public enum Geography {
    /** No category. */
    NONE            ("none"),
    /** Taiga. */
    TAIGA           ("taiga"),
    /** Extreme hills. */
    EXTREME_HILLS   ("extreme_hills"),
    /** Jungle. */
    JUNGLE          ("jungle"),
    /** Mesa. */
    MESA            ("mesa"),
    /** Plains. */
    PLAINS          ("plains"),
    /** Savanna. */
    SAVANNA         ("savanna"),
    /** Ice biome. */
    ICY             ("icy"),
    /** The biome in the End. */
    THE_END         ("the_end"),
    /** Beach. */
    BEACH           ("beach"),
    /** Forest. */
    FOREST          ("forest"),
    /** Ocean. */
    OCEAN           ("ocean"),
    /** Desert. */
    DESERT          ("desert"),
    /** River. */
    RIVER           ("river"),
    /** Swamp. */
    SWAMP           ("swamp"),
    /** Mushroom biome. */
    MUSHROOM        ("mushroom"),
    /** The biome in the Nether. */
    NETHER          ("nether"),
    /** Underground. */
    UNDERGROUND     ("underground");

    private final String key;

    Geography(String key) {
        this.key = key;
    }

    /**
     * The key of this value.
     * @return The key.
     */
    public String key() { return key; }
}
