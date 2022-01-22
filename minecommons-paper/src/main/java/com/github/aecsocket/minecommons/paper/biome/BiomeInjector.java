package com.github.aecsocket.minecommons.paper.biome;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.aecsocket.minecommons.paper.PaperEnvironment;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.kyori.adventure.key.Key;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_18_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
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

    /*
    MappedRegistry:
        private final ObjectList<T> byId = new ObjectArrayList(256);
        private final it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap<T> toId = new it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap<T>(2048);
        private final BiMap<ResourceLocation, T> storage = HashBiMap.create(2048);
        private final BiMap<ResourceKey<T>, T> keyStorage = HashBiMap.create(2048);
         */
    private final WritableRegistry<Biome> nmsBiomes;
    private final ObjectList<Biome> nmsById;
    private final Reference2IntOpenHashMap<Biome> nmsToId;
    private final BiMap<ResourceLocation, Biome> nmsStorage;
    private final BiMap<ResourceKey<Biome>, Biome> nmsKeyStorage;

    private final int vanillaSize;
    private final Map<Key, Entry> byKey = new HashMap<>();
    private final Int2ObjectMap<Entry> byId = new Int2ObjectArrayMap<>();
    private final BiMap<ResourceLocation, Entry> byNmsKey = HashBiMap.create();

    private <T> T registryField(String mojang, String def) throws NoSuchFieldException, IllegalAccessException {
        Field field = MappedRegistry.class.getDeclaredField(PaperEnvironment.map(mojang, def));
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        T result = (T) field.get(nmsBiomes);
        return result;
    }

    /**
     * Creates a biome injector.
     * @throws NoSuchFieldException If reflection setup failed.
     * @throws IllegalAccessException If reflection setup failed.
     */
    public BiomeInjector() throws NoSuchFieldException, IllegalAccessException {
        @SuppressWarnings({"deprecation", "PatternValidation"})
        var nmsBiomes = (WritableRegistry<Biome>) MinecraftServer.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        this.nmsBiomes = nmsBiomes;

        nmsById = registryField("byId", "bz");
        nmsToId = registryField("toId", "bA");
        nmsStorage = registryField("storage", "bB");
        nmsKeyStorage = registryField("keyStorage", "bC");

        int id = 0;
        for (; id < nmsById.size(); id++) {
            @SuppressWarnings("PatternValidation")
            Biome biome = nmsById.get(id);
            @SuppressWarnings("PatternValidation")
            ResourceKey<Biome> key = nmsBiomes.getResourceKey(biome).orElseThrow();
            @SuppressWarnings("PatternValidation")
            ResourceLocation location = key.location();

            @SuppressWarnings("PatternValidation")
            Entry newEntry = new Entry(
                    true, id,
                    key, biome,
                    Key.key(location.getNamespace(), location.getPath()), PaperBiomeData.from(biome)
            );
            add(newEntry);
        }
        vanillaSize = id;
    }

    private void add(Entry entry) {
        byKey.put(entry.key, entry);
        byId.put(entry.id, entry);
        byNmsKey.put(entry.resourceKey.location(), entry);
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
    public Entry get(Key key) {
        //noinspection PatternValidation
        return byKey.get(Key.key(key.namespace(), key.value()));
    }

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
        int id = existing == null ? nmsById.size() : existing.id; // TODO?
        var resource = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(key.namespace(), key.value()));
        Biome handle = biomeData.toHandle();
        Entry entry = new Entry(
                false, id,
                resource, handle,
                key, biomeData
        );
        add(entry);
        if (!nmsBiomes.containsKey(resource)) {
            nmsBiomes.registerMapping(id, resource, handle, Lifecycle.stable());
        }
        return entry;
    }

    /**
     * Uninjects a biome entry based on its namespaced key.
     * <p>
     * Note that <b>this will not remove the entries in the underlying NMS implementation.</b>
     * Be careful of memory leaks!
     * @param key The key.
     */
    public void uninject(Key key) {
        Entry entry = byKey.remove(key);
        if (entry != null) {
            byId.remove(entry.id);
            byNmsKey.remove(entry.resourceKey.location());
        }
    }

    /**
     * Uninjects all non-vanilla biomes.
     * <p>
     * <b>Note:</b> this does not restore biomes which were injected under a vanilla namespaced key.
     */
    public void uninjectAll() {
        var iter = byKey.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = iter.next().getValue();
            if (!entry.vanilla) {
                iter.remove();
                byId.remove(entry.id);

                nmsById.remove(entry.handle);
                nmsToId.removeInt(entry.id);
                nmsStorage.remove(entry.resourceKey.location());
                nmsKeyStorage.remove(entry.resourceKey);
            }
        }

        // In case we fail to restore the registry properly, warn the user.
        // This can cause memory leaks!
        int size = nmsStorage.size();
        if (size != vanillaSize)
            throw new IllegalStateException("Biome registry was not restored properly: expected " + vanillaSize + ", found " + size);
    }

    /**
     * Resends the chunk data of a biome to a player, to update the biomes.
     * @param chunk The chunk to update.
     * @param player The player.
     */
    public void resendBiomes(Chunk chunk, Player player) {
        LevelChunk nmsChunk = ((CraftChunk) chunk).getHandle();
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        var packet = new ClientboundLevelChunkWithLightPacket(nmsChunk, nmsChunk.level.getLightEngine(), null, null, true,
                nmsChunk.level.chunkPacketBlockController.shouldModify(nmsPlayer, nmsChunk));
        nmsPlayer.connection.send(packet);
    }

    public interface BiomeRemapper {
        Key remap(Entry entry);
    }

    private LevelChunk remapChunk(LevelChunk chunk, BiomeRemapper remapper) {
        Level level = chunk.level;
        ChunkPos pos = chunk.getPos();

        LevelChunkSection[] origSections = chunk.getSections();
        LevelChunkSection[] sections = new LevelChunkSection[origSections.length];
        for (int i = 0; i < origSections.length; i++) {
            var origSection = origSections[i];

            @SuppressWarnings("ConstantConditions")
            PalettedContainer<Biome> biomes = new PalettedContainer<>(nmsBiomes, nmsBiomes.getOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES, null);
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    for (int z = 0; z < 4; z++) {
                        Biome oldBiome = biomes.get(x, y, z);
                        Entry entry = byNmsKey.get(nmsBiomes.getKey(oldBiome));
                        Key newKey = remapper.remap(entry);
                        Biome newBiome = byKey.get(newKey).handle;
                        biomes.set(x, y, z, newBiome);
                    }
                }
            }
            sections[i] = new LevelChunkSection(origSection.bottomBlockY() >> 4,
                    origSection.states,
                    biomes);
        }

        return new LevelChunk(level, pos,
                chunk.getUpgradeData(),
                (LevelChunkTicks<Block>) chunk.getBlockTicks(), (LevelChunkTicks<Fluid>) chunk.getFluidTicks(),
                chunk.getInhabitedTime(), sections,
                null, chunk.getBlendingData());
    }

    public void remapBiomes(PacketEvent event, BiomeRemapper remapper) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();

        LevelChunk nmsChunk = ((CraftChunk) player.getWorld().getChunkAt(
                packet.getIntegers().read(0),
                packet.getIntegers().read(1)
        )).getHandle();
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        LevelChunk remappedChunk = remapChunk(nmsChunk, remapper);
        boolean modifyBlocks = nmsChunk.level.chunkPacketBlockController.shouldModify(nmsPlayer, nmsChunk);
        ClientboundLevelChunkWithLightPacket nmsPacket = new ClientboundLevelChunkWithLightPacket(
                remappedChunk,
                nmsChunk.level.getLightEngine(), null, null, true, modifyBlocks
        );

        packet.getModifier().write(2, nmsPacket.getChunkData());
    }
}
