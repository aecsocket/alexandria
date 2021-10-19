package com.gitlab.aecsocket.minecommons.paper.biome;

import com.gitlab.aecsocket.minecommons.paper.PaperEnvironment;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Controls injecting biomes into the Paper global biome registry.
 */
public final class BiomeInjector {
    private final Map<Biome, PaperBiomeData> biomeData = new HashMap<>();
    private final Int2ObjectMap<Biome> biomeIds = new Int2ObjectArrayMap<>();

    /**
     * Creates and initializes the injector.
     * @throws NoSuchFieldException If reflection calls failed.
     * @throws IllegalAccessException If reflection calls failed.
     */
    public BiomeInjector() throws NoSuchFieldException, IllegalAccessException {
        for (var biome : Biome.values())
            biomeData.put(biome, PaperBiomeData.from(biome));

        var registry = BuiltinRegistries.BIOME;
        Field idsField = Biomes.class.getDeclaredField(PaperEnvironment.map("TO_NAME", "c"));
        idsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var ids = (Int2ObjectMap<ResourceKey<net.minecraft.world.level.biome.Biome>>) idsField.get(null);
        for (var entry : ids.int2ObjectEntrySet())
            biomeIds.put(entry.getIntKey(), CraftBlock.biomeBaseToBiome(registry, registry.get(entry.getValue())));
    }

    /**
     * Gets the biome -> biome data mappings.
     * @return The mappings.
     */
    public Map<Biome, PaperBiomeData> biomeData() { return biomeData; }

    /**
     * Gets the map of all integer IDs to their Bukkit biomes.
     * @return The mappings.
     */
    public Int2ObjectMap<Biome> biomeIds() { return biomeIds; }

    /**
     * Gets a biome data from a Bukkit biome.
     * @param biome The biome.
     * @return The biome data.
     */
    public PaperBiomeData biomeData(Biome biome) {
        return biomeData.get(biome);
    }
}
