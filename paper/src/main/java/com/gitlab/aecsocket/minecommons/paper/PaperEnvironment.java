package com.gitlab.aecsocket.minecommons.paper;

/**
 * Provides helper functions for using the provided Paper environment.
 */
public final class PaperEnvironment {
    private PaperEnvironment() {}

    /**
     * The mapping that is used by the server.
     */
    public enum Mapping {
        /** Mojang-mapped names. */
        MOJANG,
        /** Spigot-mapped names. */
        SPIGOT,
        /** An unknown mapping. */
        UNKNOWN
    }

    private static Mapping mapping;

    static {
        try {
            Class.forName("net.minecraft.Util");
            mapping = Mapping.MOJANG;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("net.minecraft.SystemUtils");
                mapping = Mapping.SPIGOT;
            } catch (ClassNotFoundException f) {
                mapping = Mapping.UNKNOWN;
            }
        }
    }

    /**
     * Gets the mapping currently in use by the server.
     * @return The mapping.
     */
    public static Mapping mapping() { return mapping; }

    /**
     * Gets a mapped name, either from the Mojang mappings or another mapping.
     * @param mojang The Mojang mapped name.
     * @param def The fallback mapped name.
     * @return The name.
     */
    public static String map(String mojang, String def) {
        return mapping == Mapping.MOJANG ? mojang : def;
    }
}
