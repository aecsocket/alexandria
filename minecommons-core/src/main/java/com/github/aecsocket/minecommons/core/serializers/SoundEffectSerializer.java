package com.github.aecsocket.minecommons.core.serializers;

import net.kyori.adventure.sound.Sound;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;

import com.github.aecsocket.minecommons.core.effect.SoundEffect;

/**
 * Type serializer for a {@link SoundEffect}.
 */
public class SoundEffectSerializer implements TypeSerializer<SoundEffect> {
    /** A singleton instance of this serializer. */
    public static final SoundEffectSerializer INSTANCE = new SoundEffectSerializer();

    /** The key for the field {@code dropoff}. */
    public static final String DROPOFF = "dropoff";
    /** The key for the field {@code range}. */
    public static final String RANGE = "range";
    /** The key for the field {@code speed}. */
    public static final String SPEED = "speed";

    @Override
    public void serialize(Type type, @Nullable SoundEffect obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(obj.sound());
            node.node(DROPOFF).set(obj.dropoff());
            node.node(RANGE).set(obj.range());
            if (Double.compare(obj.speed(), SoundEffect.SPEED) != 0)
                node.node(SPEED).set(obj.speed());
        }
    }

    @Override
    public SoundEffect deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return SoundEffect.soundEffect(
                require(node, Sound.class),
                node.node(DROPOFF).getDouble(0),
                node.node(RANGE).getDouble(2),
                node.node(SPEED).getDouble(SoundEffect.SPEED)
        );
    }
}
