package com.gitlab.aecsocket.minecommons.paper.biome;

import com.gitlab.aecsocket.minecommons.core.biome.BiomeEffects;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;

import java.util.Optional;

import static com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3.rgb;

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
     * Creates biome effects from a Bukkit biome.
     * @param biome The biome.
     * @return The biome effects.
     */
    public static PaperBiomeEffects from(Biome biome) {
        return from(CraftBlock.biomeToBiomeBase(BuiltinRegistries.BIOME, biome).getSpecialEffects());
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
