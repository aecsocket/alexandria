package com.gitlab.aecsocket.minecommons.core.biome;

/**
 * Wrapper for a biome's data.
 */
public interface BiomeData {
    /**
     * Gets the precipitation type that occurs in this biome.
     * @return The precipitation.
     */
    Precipitation precipitation();

    /**
     * Gets the geography/category that this biome falls under.
     * @return The geography.
     */
    Geography geography();

    /**
     * Gets the terrain depth.
     * @return The depth.
     */
    float depth();

    /**
     * Gets the terrain scale.
     * @return The scale.
     */
    float scale();

    /**
     * Gets the temperature.
     * @return The temperature.
     */
    float temperature();

    /**
     * Gets the humidity.
     * @return The humidity.
     */
    float humidity();

    /**
     * Gets the biome effects.
     * @return The effects.
     */
    BiomeEffects effects();

    /**
     * Gets the mob spawning settings.
     * @return The mob spawning settings.
     */
    Object mobs();

    /**
     * Gets the generation settings.
     * @return The generation settings.
     */
    Object generation();
}
