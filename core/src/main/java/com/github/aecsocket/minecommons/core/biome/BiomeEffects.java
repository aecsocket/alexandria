package com.github.aecsocket.minecommons.core.biome;

import java.util.Optional;

import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * Effects that are applied to a player in a biome, visuals and audio.
 */
public interface BiomeEffects {
    /**
     * Gets the fog color.
     * @return The fog color.
     */
    Vector3 fog();

    /**
     * Gets the water color.
     * @return The water color.
     */
    Vector3 water();

    /**
     * Gets the water fog color.
     * @return The water fog color.
     */
    Vector3 waterFog();

    /**
     * Gets the sky color.
     * @return The sky color.
     */
    Vector3 sky();

    /**
     * Gets an optional of the foliage color.
     * @return The foliage color.
     */
    Optional<Vector3> foliage();

    /**
     * Gets an optional of the grass color.
     * @return The grass color.
     */
    Optional<Vector3> grass();

    /**
     * Gets the grass color modifier.
     * @return The grass color modifier.
     */
    Object grassModifier();

    /**
     * Gets the particle spawning options.
     * @return The particle spawning options.
     */
    Optional<?> particles();

    /**
     * Gets the ambient sound.
     * @return The ambient sound.
     */
    Optional<?> ambientSound();

    /**
     * Gets the mood/cave sound.
     * @return The mood/cave sound.
     */
    Optional<?> moodSound();

    /**
     * Gets the additions sounds.
     * @return The additions sound.
     */
    Optional<?> additionsSound();

    /**
     * Gets the music.
     * @return The music.
     */
    Optional<?> music();
}
