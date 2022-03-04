package com.github.aecsocket.minecommons.paper.biome;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlock;
import org.checkerframework.checker.nullness.qual.Nullable;

import static com.github.aecsocket.minecommons.core.vector.cartesian.Vector3.rgb;

import java.util.Optional;

import com.github.aecsocket.minecommons.core.biome.BiomeEffects;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * Paper implementation of biome effects, wrapping a BiomeFog.
 */
public record PaperBiomeEffects(
    Vector3 fog,
    Vector3 water,
    Vector3 waterFog,
    Vector3 sky,
    Optional<Vector3> foliage,
    Optional<Vector3> grass,
    BiomeSpecialEffects.GrassColorModifier grassModifier,
    Optional<AmbientParticleSettings> particles,
    Optional<SoundEvent> ambientSound,
    Optional<AmbientMoodSettings> moodSound,
    Optional<AmbientAdditionsSettings> additionsSound,
    Optional<Music> music
) implements BiomeEffects {
    /**
     * Creates a copy with the specified value changed.
     * @param fog The new fog.
     * @return The copy.
     */
    public PaperBiomeEffects fog(Vector3 fog) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, particles, ambientSound, moodSound, additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param water The new water.
     * @return The copy.
     */
    public PaperBiomeEffects water(Vector3 water) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, particles, ambientSound, moodSound, additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param waterFog The new water fog.
     * @return The copy.
     */
    public PaperBiomeEffects waterFog(Vector3 waterFog) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, particles, ambientSound, moodSound, additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param sky The new sky.
     * @return The copy.
     */
    public PaperBiomeEffects sky(Vector3 sky) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, particles, ambientSound, moodSound, additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param foliage The new foliage.
     * @return The copy.
     */
    public PaperBiomeEffects foliage(@Nullable Vector3 foliage) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, Optional.ofNullable(foliage), grass, grassModifier, particles, ambientSound, moodSound, additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param grass The new grass.
     * @return The copy.
     */
    public PaperBiomeEffects grass(@Nullable Vector3 grass) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, Optional.ofNullable(grass), grassModifier, particles, ambientSound, moodSound, additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param grassModifier The new grass modifier.
     * @return The copy.
     */
    public PaperBiomeEffects grassModifier(BiomeSpecialEffects.GrassColorModifier grassModifier) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, particles, ambientSound, moodSound, additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param particles The new particles.
     * @return The copy.
     */
    public PaperBiomeEffects particles(@Nullable AmbientParticleSettings particles) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, Optional.ofNullable(particles), ambientSound, moodSound, additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param ambientSound The new ambient sound.
     * @return The copy.
     */
    public PaperBiomeEffects ambientSound(@Nullable SoundEvent ambientSound) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, particles, Optional.ofNullable(ambientSound), moodSound, additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param moodSound The new mood sound.
     * @return The copy.
     */
    public PaperBiomeEffects moodSound(@Nullable AmbientMoodSettings moodSound) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, particles, ambientSound, Optional.ofNullable(moodSound), additionsSound, music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param additionsSound The new additions sound.
     * @return The copy.
     */
    public PaperBiomeEffects additionsSound(@Nullable AmbientAdditionsSettings additionsSound) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, particles, ambientSound, moodSound, Optional.ofNullable(additionsSound), music);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param music The new music.
     * @return The copy.
     */
    public PaperBiomeEffects music(@Nullable Music music) {
        return new PaperBiomeEffects(fog, water, waterFog, sky, foliage, grass, grassModifier, particles, ambientSound, moodSound, additionsSound, Optional.ofNullable(music));
    }

    /**
     * Creates biome effects from an NMS handle.
     * @param nms The NMS handle.
     * @return The biome effects.
     */
    public static PaperBiomeEffects from(BiomeSpecialEffects nms) {
        return new PaperBiomeEffects(
            rgb(nms.getFogColor()),
            rgb(nms.getWaterColor()),
            rgb(nms.getWaterFogColor()),
            rgb(nms.getSkyColor()),
            nms.getFoliageColorOverride().flatMap(v -> Optional.of(rgb(v))),
            nms.getGrassColorOverride().flatMap(v -> Optional.of(rgb(v))),
            nms.getGrassColorModifier(),
            nms.getAmbientParticleSettings(),
            nms.getAmbientLoopSoundEvent(),
            nms.getAmbientMoodSettings(),
            nms.getAmbientAdditionsSettings(),
            nms.getBackgroundMusic()
        );
    }

    /**
     * Creates biome effects from a Paper biome.
     * @param biome The biome.
     * @return The biome effects.
     */
    public static PaperBiomeEffects from(Biome biome) {
        return from(CraftBlock.biomeToBiomeBase(BuiltinRegistries.BIOME, biome).value().getSpecialEffects());
    }

    /**
     * Converts this data to an NMS handle.
     * @return The NMS handle.
     */
    public BiomeSpecialEffects toHandle() {
        var builder = new BiomeSpecialEffects.Builder()
            .fogColor(fog.rgb())
            .waterColor(water.rgb())
            .waterFogColor(waterFog.rgb())
            .skyColor(sky.rgb())
            .grassColorModifier(grassModifier);
        foliage.ifPresent(v -> builder.foliageColorOverride(v.rgb()));
        grass.ifPresent(v -> builder.grassColorOverride(v.rgb()));
        particles.ifPresent(builder::ambientParticle);
        ambientSound.ifPresent(builder::ambientLoopSound);
        moodSound.ifPresent(builder::ambientMoodSound);
        additionsSound.ifPresent(builder::ambientAdditionsSound);
        music.ifPresent(builder::backgroundMusic);
        return builder.build();
    }
}
