package com.gitlab.aecsocket.minecommons.paper.biome;

import com.gitlab.aecsocket.minecommons.core.biome.BiomeEffects;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.level.biome.BiomeFog;
import net.minecraft.world.level.biome.BiomeParticles;
import net.minecraft.world.level.biome.CaveSound;
import net.minecraft.world.level.biome.CaveSoundSettings;
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
        BiomeFog.GrassColor grassModifier,
        Optional<BiomeParticles> particles,
        Optional<SoundEffect> ambientSound,
        Optional<CaveSoundSettings> moodSound,
        Optional<CaveSound> additionsSound,
        Optional<Music> music
) implements BiomeEffects {
    /**
     * Creates biome effects from an NMS handle.
     * @param nms The NMS handle.
     * @return The biome effects.
     */
    public static PaperBiomeEffects from(BiomeFog nms) {
        return new PaperBiomeEffects(
                rgb(nms.a()), // fog
                rgb(nms.b()), // water
                rgb(nms.c()), // waterFog
                rgb(nms.d()), // sky
                nms.e().flatMap(v -> Optional.of(rgb(v))), // foliage
                nms.f().flatMap(v -> Optional.of(rgb(v))), // grass
                nms.g(), // grassModifier
                nms.h(), // particles
                nms.i(), // ambientSound
                nms.j(), // moodSound
                nms.k(), // additionsSound
                nms.l() // music
        );
    }

    /**
     * Creates biome effects from a Bukkit biome.
     * @param biome The biome.
     * @return The biome effects.
     */
    public static PaperBiomeEffects from(Biome biome) {
        return from(CraftBlock.biomeToBiomeBase(RegistryGeneration.i, biome).l());
    }

    /**
     * Converts this data to an NMS handle.
     * @return The NMS handle.
     */
    public BiomeFog toHandle() {
        var builder = new BiomeFog.a()
                .a(fog.rgb())
                .b(water.rgb())
                .c(waterFog.rgb())
                .d(sky.rgb())
                .a(grassModifier);
        foliage.ifPresent(v -> builder.e(v.rgb()));
        grass.ifPresent(v -> builder.f(v.rgb()));
        particles.ifPresent(builder::a);
        ambientSound.ifPresent(builder::a);
        moodSound.ifPresent(builder::a);
        additionsSound.ifPresent(builder::a);
        music.ifPresent(builder::a);
        return builder.a();
    }
}
