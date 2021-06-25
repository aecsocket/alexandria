package com.gitlab.aecsocket.minecommons.paper.serializers;

import com.gitlab.aecsocket.minecommons.paper.display.PreciseSound;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

import static com.gitlab.aecsocket.minecommons.core.serializers.Serializers.require;

/**
 * Type serializer for a {@link PreciseSound}.
 */
public class PreciseSoundSerializer implements TypeSerializer<PreciseSound> {
    /** A singleton instance of this serializer. */
    public static final PreciseSoundSerializer INSTANCE = new PreciseSoundSerializer();

    @Override
    public void serialize(Type type, @Nullable PreciseSound obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node("name").set(obj.name());
            node.node("source").set(obj.source());
            node.node("volume").set(obj.volume());
            node.node("pitch").set(obj.pitch());
            node.node("dropoff").set(Math.sqrt(obj.dropoffSqr()));
            node.node("range").set(Math.sqrt(obj.rangeSqr()));
            if (Double.compare(obj.speed(), PreciseSound.SPEED_MT) != 0)
                node.node("speed").set(Math.sqrt(obj.speed()));
        }
    }

    @Override
    public PreciseSound deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return PreciseSound.of(
                require(node.node("name"), Key.class),
                node.node("source").get(Sound.Source.class, Sound.Source.MASTER),
                node.node("volume").getFloat(1f),
                node.node("pitch").getFloat(1f),
                node.node("dropoff").getDouble(1),
                node.node("range").getDouble(1),
                node.node("speed").getDouble(PreciseSound.SPEED_MS)
        );
    }
}
