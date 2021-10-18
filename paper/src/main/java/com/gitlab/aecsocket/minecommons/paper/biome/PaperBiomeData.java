package com.gitlab.aecsocket.minecommons.paper.biome;

import com.gitlab.aecsocket.minecommons.core.CollectionBuilder;
import com.gitlab.aecsocket.minecommons.core.biome.BiomeData;
import com.gitlab.aecsocket.minecommons.core.biome.Geography;
import com.gitlab.aecsocket.minecommons.core.biome.Precipitation;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;

import java.util.HashMap;

/**
 * Paper implementation of biome data, wrapping a BiomeBase.
 */
public record PaperBiomeData(
        Precipitation precipitation,
        Geography geography,
        float depth,
        float scale,
        float temperature,
        float humidity,
        PaperBiomeEffects effects,
        BiomeSettingsMobs mobs,
        BiomeSettingsGeneration generation
) implements BiomeData {
    /**
     * A map of NMS precipitation enum values to ours.
     */
    public static final BiMap<BiomeBase.Precipitation, Precipitation> PRECIPITATION = HashBiMap.create(CollectionBuilder.map(new HashMap<BiomeBase.Precipitation, Precipitation>())
            .put(BiomeBase.Precipitation.a, Precipitation.NONE)
            .put(BiomeBase.Precipitation.b, Precipitation.RAIN)
            .put(BiomeBase.Precipitation.c, Precipitation.SNOW)
            .get());

    /**
     * A map of NMS geography enum values to ours.
     */
    public static final BiMap<BiomeBase.Geography, Geography> GEOGRAPHY = HashBiMap.create(CollectionBuilder.map(new HashMap<BiomeBase.Geography, Geography>())
            .put(BiomeBase.Geography.a, Geography.NONE)
            .put(BiomeBase.Geography.b, Geography.TAIGA)
            .put(BiomeBase.Geography.c, Geography.EXTREME_HILLS)
            .put(BiomeBase.Geography.d, Geography.JUNGLE)
            .put(BiomeBase.Geography.e, Geography.MESA)
            .put(BiomeBase.Geography.f, Geography.PLAINS)
            .put(BiomeBase.Geography.g, Geography.SAVANNA)
            .put(BiomeBase.Geography.h, Geography.ICY)
            .put(BiomeBase.Geography.i, Geography.THE_END)
            .put(BiomeBase.Geography.j, Geography.BEACH)
            .put(BiomeBase.Geography.k, Geography.FOREST)
            .put(BiomeBase.Geography.l, Geography.OCEAN)
            .put(BiomeBase.Geography.m, Geography.DESERT)
            .put(BiomeBase.Geography.n, Geography.RIVER)
            .put(BiomeBase.Geography.o, Geography.SWAMP)
            .put(BiomeBase.Geography.p, Geography.MUSHROOM)
            .put(BiomeBase.Geography.q, Geography.NETHER)
            .put(BiomeBase.Geography.r, Geography.UNDERGROUND)
            .get());

    /**
     * Creates biome data from an NMS handle.
     * @param nms The NMS handle.
     * @return The biome data.
     */
    public static PaperBiomeData from(BiomeBase nms) {
        return new PaperBiomeData(
                PRECIPITATION.get(nms.c()),
                GEOGRAPHY.get(nms.t()),
                nms.h(), // depth
                nms.j(), // scale
                nms.k(), // temperature
                nms.getHumidity(),
                PaperBiomeEffects.from(nms.l()), // settings/fog
                nms.b(), // mobs
                nms.e() // generation
        );
    }

    /**
     * Creates biome data from a Bukkit biome.
     * @param biome The biome.
     * @return The biome data.
     */
    public static PaperBiomeData from(Biome biome) {
        return from(CraftBlock.biomeToBiomeBase(RegistryGeneration.i, biome));
    }

    /**
     * Converts this data to an NMS handle.
     * @return The NMS handle.
     */
    public BiomeBase toHandle() {
        return new BiomeBase.a()
                .a(PRECIPITATION.inverse().get(precipitation))
                .a(GEOGRAPHY.inverse().get(geography))
                .a(depth)
                .b(scale)
                .c(temperature)
                .a(BiomeBase.TemperatureModifier.a)
                .d(humidity)
                .a(effects.toHandle())
                .a(mobs)
                .a(generation)
                .a();
    }
}
