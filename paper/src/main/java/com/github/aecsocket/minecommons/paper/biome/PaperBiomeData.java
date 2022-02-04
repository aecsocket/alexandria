package com.github.aecsocket.minecommons.paper.biome;

import com.github.aecsocket.minecommons.core.biome.BiomeData;
import com.github.aecsocket.minecommons.core.biome.Geography;
import com.github.aecsocket.minecommons.core.biome.Precipitation;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock;

import java.util.HashMap;
import java.util.Map;

/**
 * Paper implementation of biome data, wrapping a BiomeBase.
 */
public record PaperBiomeData(
    Precipitation precipitation,
    Geography geography,
    float temperature,
    float humidity,
    PaperBiomeEffects effects,
    MobSpawnSettings mobs,
    BiomeGenerationSettings generation
) implements BiomeData {
    /**
     * A map of NMS precipitation enum values to ours.
     */
    public static final BiMap<Biome.Precipitation, Precipitation> PRECIPITATION;

    /**
     * A map of NMS geography enum values to ours.
     */
    public static final BiMap<Biome.BiomeCategory, Geography> GEOGRAPHY;

    static {
        Map<Biome.Precipitation, Precipitation> precipitation = new HashMap<>();
        for (int i = 0; i < Biome.Precipitation.values().length; i++)
            precipitation.put(Biome.Precipitation.values()[i], Precipitation.values()[i]);
        PRECIPITATION = HashBiMap.create(precipitation);

        Map<Biome.BiomeCategory, Geography> geography = new HashMap<>();
        for (int i = 0; i < Biome.BiomeCategory.values().length; i++)
            geography.put(Biome.BiomeCategory.values()[i], Geography.values()[i]);
        GEOGRAPHY = HashBiMap.create(geography);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param precipitation The new precipitation.
     * @return The copy.
     */
    public PaperBiomeData precipitation(Precipitation precipitation) {
        return new PaperBiomeData(precipitation, geography, temperature, humidity, effects, mobs, generation);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param geography The new geography.
     * @return The copy.
     */
    public PaperBiomeData geography(Geography geography) {
        return new PaperBiomeData(precipitation, geography, temperature, humidity, effects, mobs, generation);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param temperature The new temperature.
     * @return The copy.
     */
    public PaperBiomeData temperature(float temperature) {
        return new PaperBiomeData(precipitation, geography, temperature, humidity, effects, mobs, generation);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param humidity The new humidity.
     * @return The copy.
     */
    public PaperBiomeData humidity(float humidity) {
        return new PaperBiomeData(precipitation, geography, temperature, humidity, effects, mobs, generation);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param effects The new effects.
     * @return The copy.
     */
    public PaperBiomeData effects(PaperBiomeEffects effects) {
        return new PaperBiomeData(precipitation, geography, temperature, humidity, effects, mobs, generation);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param mobs The new mobs.
     * @return The copy.
     */
    public PaperBiomeData mobs(MobSpawnSettings mobs) {
        return new PaperBiomeData(precipitation, geography, temperature, humidity, effects, mobs, generation);
    }

    /**
     * Creates a copy with the specified value changed.
     * @param generation The new generation.
     * @return The copy.
     */
    public PaperBiomeData generation(BiomeGenerationSettings generation) {
        return new PaperBiomeData(precipitation, geography, temperature, humidity, effects, mobs, generation);
    }

    /**
     * Creates biome data from an NMS handle.
     * @param nms The NMS handle.
     * @return The biome data.
     */
    public static PaperBiomeData from(Biome nms) {
        Precipitation precipitation = PRECIPITATION.get(nms.getPrecipitation());
        Geography geography = GEOGRAPHY.get(nms.getBiomeCategory());
        if (precipitation == null) throw new IllegalArgumentException("NMS precipitation `" + nms.getPrecipitation() + "` cannot be mapped");
        if (geography == null) throw new IllegalArgumentException("NMS geography `" + nms.getBiomeCategory() + "` cannot be mapped");
        return new PaperBiomeData(
            precipitation,
            geography,
            nms.getBaseTemperature(),
            nms.getDownfall(),
            PaperBiomeEffects.from(nms.getSpecialEffects()),
            nms.getMobSettings(),
            nms.getGenerationSettings()
        );
    }

    /**
     * Creates biome data from a Paper biome.
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
        Biome.Precipitation precipitation = PRECIPITATION.inverse().get(this.precipitation);
        Biome.BiomeCategory geography = GEOGRAPHY.inverse().get(this.geography);
        if (precipitation == null) throw new IllegalArgumentException("Minecommons precipitation `" + this.precipitation + "` cannot be mapped");
        if (geography == null) throw new IllegalArgumentException("Minecommons geography `" + this.geography + "` cannot be mapped");
        return new Biome.BiomeBuilder()
            .precipitation(precipitation)
            .biomeCategory(geography)
            .temperature(temperature)
            .temperatureAdjustment(Biome.TemperatureModifier.NONE)
            .downfall(humidity)
            .specialEffects(effects.toHandle())
            .mobSpawnSettings(mobs)
            .generationSettings(generation)
            .build();
    }
}
