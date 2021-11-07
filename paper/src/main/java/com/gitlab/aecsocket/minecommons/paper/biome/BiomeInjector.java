package com.gitlab.aecsocket.minecommons.paper.biome;

import com.gitlab.aecsocket.minecommons.paper.PaperEnvironment;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Controls injecting biomes into the Paper global biome registry.
 */
public final class BiomeInjector {
    /**
     * An entry in the biome registry, along with biome injector data.
     * @param vanilla If the entry is for a vanilla biome.
     * @param id The integer ID used in chunk packets.
     * @param resourceKey The NMS resource key used to access this biome.
     * @param handle The NMS handle of the value.
     * @param key The namespaced key used to access this biome.
     * @param biomeData The wrapped biome data.
     */
    public record Entry(boolean vanilla, int id,
                        ResourceKey<Biome> resourceKey, Biome handle,
                        Key key, PaperBiomeData biomeData) {}

    private final Int2ObjectMap<ResourceKey<Biome>> nmsBiomeIds;
    private final WritableRegistry<Biome> nmsGlobalBiomes;
    private final WritableRegistry<Biome> nmsCodecBiomes;

    private final Map<Key, Entry> byKey = new HashMap<>();
    private final Int2ObjectMap<Entry> byId = new Int2ObjectArrayMap<>();

    /**
     * Creates a biome injector.
     * @throws NoSuchFieldException If reflection setup failed.
     * @throws IllegalAccessException If reflection setup failed.
     */
    public BiomeInjector() throws NoSuchFieldException, IllegalAccessException {
        Field nmsBiomeIdsField = Biomes.class.getDeclaredField(PaperEnvironment.map("TO_NAME", "c"));
        nmsBiomeIdsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var nmsBiomeIds = (Int2ObjectMap<ResourceKey<Biome>>) nmsBiomeIdsField.get(null);
        this.nmsBiomeIds = nmsBiomeIds;

        nmsGlobalBiomes = (WritableRegistry<Biome>) BuiltinRegistries.BIOME;

        @SuppressWarnings("deprecation")
        var nmsCodecBiomes = (WritableRegistry<Biome>) MinecraftServer.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        this.nmsCodecBiomes = nmsCodecBiomes;

        for (var entry : nmsBiomeIds.int2ObjectEntrySet()) {
            @SuppressWarnings("PatternValidation")
            ResourceKey<Biome> resource = entry.getValue();
            @SuppressWarnings("PatternValidation")
            ResourceLocation location = resource.location();
            Biome handle = nmsGlobalBiomes.get(resource);
            @SuppressWarnings({"ConstantConditions", "PatternValidation"})
            Entry addEntry = new Entry(
                    true, entry.getIntKey(),
                    resource, handle,
                    Key.key(location.getNamespace(), location.getPath()), PaperBiomeData.from(handle)
            );
            add(addEntry);
        }
    }

    private void add(Entry entry) {
        byKey.put(entry.key, entry);
        byId.put(entry.id, entry);
    }

    /**
     * Gets a map of namespaced key -> entry.
     * @return The map.
     */
    public Map<Key, Entry> byKey() { return new HashMap<>(byKey); }

    /**
     * Gets a map of internal ID -> entry.
     * @return The map.
     */
    public Int2ObjectMap<Entry> byId() { return new Int2ObjectArrayMap<>(byId); }

    /**
     * Gets a biome entry by its namespaced key.
     * @param key The key.
     * @return The entry.
     */
    public Entry get(Key key) { return byKey.get(key); }

    /**
     * Gets a biome entry by its internal ID.
     * @param id The ID.
     * @return The entry.
     */
    public Entry get(int id) { return byId.get(id); }

    /**
     * Injects a biome into the global registry.
     * @param key The key to inject under.
     * @param biomeData The biome to inject.
     * @return The biome entry.
     */
    public Entry inject(Key key, PaperBiomeData biomeData) {
        var existing = byKey.get(key);
        int id;
        if (existing == null)
            for (id = 0; nmsBiomeIds.containsKey(id); id++);
        else {
            id = existing.id;
        }
        var resource = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(key.namespace(), key.value()));
        Biome handle = biomeData.toHandle();
        Entry entry = new Entry(
                false, id,
                resource, handle,
                key, biomeData
        );
        add(entry);
        if (!nmsGlobalBiomes.containsKey(resource)) {
            nmsGlobalBiomes.registerMapping(id, resource, handle, Lifecycle.stable());
            nmsCodecBiomes.registerMapping(id, resource, handle, Lifecycle.stable());
        }
        nmsBiomeIds.put(id, resource);
        return entry;
    }

    /**
     * Uninjects a biome entry based on its namespaced key.
     * @param key The key.
     */
    public void uninject(Key key) {
        Entry entry = byKey.remove(key);
        if (entry != null)
            byId.remove(entry.id);
    }

    /**
     * Uninjects all non-vanilla biomes. <b>Note:</b> this does not restore biomes which were injected under a
     * vanilla namespaced key.
     */
    public void uninjectAll() {
        var iter = byKey.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = iter.next().getValue();
            if (!entry.vanilla) {
                iter.remove();
                byId.remove(entry.id);
                nmsBiomeIds.remove(entry.id);
            }
        }
    }

    /**
     * Resends the chunk data of a biome to a player, to update the biomes.
     * @param chunk The chunk to update.
     * @param player The player.
     */
    public void resendBiomes(Chunk chunk, Player player) {
        LevelChunk nmsChunk = ((CraftChunk) chunk).getHandle();
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        var packet = new ClientboundLevelChunkPacket(nmsChunk, nmsChunk.level.chunkPacketBlockController.shouldModify(nmsPlayer, nmsChunk));
        nmsPlayer.connection.send(packet);
    }
}
