package com.gitlab.aecsocket.minecommons.paper.biome;

import com.gitlab.aecsocket.minecommons.core.CollectionBuilder;
import com.gitlab.aecsocket.minecommons.core.biome.BiomeData;
import com.gitlab.aecsocket.minecommons.core.biome.Geography;
import com.gitlab.aecsocket.minecommons.core.biome.Precipitation;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
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
        MobSpawnSettings mobs,
        BiomeGenerationSettings generation
) implements BiomeData {
    /**
     * A map of NMS precipitation enum values to ours.
     */
    public static final BiMap<Biome.Precipitation, Precipitation> PRECIPITATION = HashBiMap.create(CollectionBuilder.map(new HashMap<Biome.Precipitation, Precipitation>())
            .put(Biome.Precipitation.NONE, Precipitation.NONE)
            .put(Biome.Precipitation.RAIN, Precipitation.RAIN)
            .put(Biome.Precipitation.SNOW, Precipitation.SNOW)
            .get());

    /**
     * A map of NMS geography enum values to ours.
     */
    public static final BiMap<Biome.BiomeCategory, Geography> GEOGRAPHY = HashBiMap.create(CollectionBuilder.map(new HashMap<Biome.BiomeCategory, Geography>())
            .put(Biome.BiomeCategory.NONE, Geography.NONE)
            .put(Biome.BiomeCategory.TAIGA, Geography.TAIGA)
            .put(Biome.BiomeCategory.EXTREME_HILLS, Geography.EXTREME_HILLS)
            .put(Biome.BiomeCategory.JUNGLE, Geography.JUNGLE)
            .put(Biome.BiomeCategory.MESA, Geography.MESA)
            .put(Biome.BiomeCategory.PLAINS, Geography.PLAINS)
            .put(Biome.BiomeCategory.SAVANNA, Geography.SAVANNA)
            .put(Biome.BiomeCategory.ICY, Geography.ICY)
            .put(Biome.BiomeCategory.THEEND, Geography.THE_END)
            .put(Biome.BiomeCategory.BEACH, Geography.BEACH)
            .put(Biome.BiomeCategory.FOREST, Geography.FOREST)
            .put(Biome.BiomeCategory.OCEAN, Geography.OCEAN)
            .put(Biome.BiomeCategory.DESERT, Geography.DESERT)
            .put(Biome.BiomeCategory.RIVER, Geography.RIVER)
            .put(Biome.BiomeCategory.SWAMP, Geography.SWAMP)
            .put(Biome.BiomeCategory.MUSHROOM, Geography.MUSHROOM)
            .put(Biome.BiomeCategory.NETHER, Geography.NETHER)
            .put(Biome.BiomeCategory.UNDERGROUND, Geography.UNDERGROUND)
            .get());

    /**
     * Creates biome data from an NMS handle.
     * @param nms The NMS handle.
     * @return The biome data.
     */
    public static PaperBiomeData from(Biome nms) {
        return new PaperBiomeData(
                PRECIPITATION.get(nms.getPrecipitation()),
                GEOGRAPHY.get(nms.getBiomeCategory()),
                nms.getDepth(),
                nms.getScale(),
                nms.getBaseTemperature(),
                nms.getDownfall(),
                PaperBiomeEffects.from(nms.getSpecialEffects()),
                nms.getMobSettings(),
                nms.getGenerationSettings()
        );
    }

    /**
     * Creates biome data from a Bukkit biome.
     * @param biome The biome.
     * @return The biome data.
     */
    public static PaperBiomeData from(org.bukkit.block.Biome biome) {
        return from(CraftBlock.biomeToBiomeBase(BuiltinRegistries.BIOME, biome));
    }

    /**
     * Converts this data to an NMS handle.
     * @return The NMS handle.
     */
    public net.minecraft.world.level.biome.Biome toHandle() {
        return new Biome.BiomeBuilder()
                .precipitation(PRECIPITATION.inverse().get(precipitation))
                .biomeCategory(GEOGRAPHY.inverse().get(geography))
                .depth(depth)
                .scale(scale)
                .temperature(temperature)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .downfall(humidity)
                .specialEffects(effects.toHandle())
                .mobSpawnSettings(mobs)
                .generationSettings(generation)
                .build();
    }
}
