package com.github.aecsocket.minecommons.core.serializers;

import com.github.aecsocket.minecommons.core.bounds.Bound;
import com.github.aecsocket.minecommons.core.bounds.Box;
import com.github.aecsocket.minecommons.core.bounds.Compound;
import com.github.aecsocket.minecommons.core.bounds.Sphere;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

/**
 * Type serializer for a {@link Bound}, using the inbuilt bound types.
 */
public class BasicBoundSerializer implements TypeSerializer<Bound> {
    /** A singleton instance of this serializer. */
    public static final BasicBoundSerializer INSTANCE = new BasicBoundSerializer();

    private static final String
        TYPE = "type",
        SPHERE = "sphere",
        BOX = "box",
        CENTER = "center",
        RADIUS = "radius",
        MIN = "min",
        MAX = "max",
        ANGLE = "angle";

    @Override
    public void serialize(Type type, @Nullable Bound obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            if (obj instanceof Compound compound) {
                node.setList(Bound.class, Arrays.asList(compound.bounds()));
            } else if (obj instanceof Sphere sphere) {
                node.node(TYPE).set(SPHERE);
                if (!Vector3.ZERO.equals(sphere.center()))
                    node.node(CENTER).set(sphere.center());
                node.node(RADIUS).set(sphere.radius());
            } else if (obj instanceof Box box) {
                node.node(TYPE).set(BOX);
                node.node(MIN).set(box.min());
                node.node(MAX).set(box.max());
                if (Double.compare(box.angle(), 0) != 0)
                    node.node(ANGLE).set(box.angle());
            }
            throw new SerializationException(node, type, "Unknown bound type " + obj.getClass().getName());
        }
    }

    @Override
    public Bound deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.isList()) {
            return Compound.compound(require(node, Bound[].class));
        } else if (node.isMap()) {
            String typeName = require(node.node(TYPE), String.class);
            switch (typeName) {
                case SPHERE -> Sphere.sphere(
                    node.node(CENTER).get(Vector3.class, Vector3.ZERO),
                    require(node.node(RADIUS), double.class)
                );
                case BOX -> Box.box(
                    require(node.node(MIN), Vector3.class),
                    require(node.node(MAX), Vector3.class),
                    node.node(ANGLE).getDouble()
                );
            }
            throw new SerializationException(node, type, "Unknown bound type `" + typeName + "`");
        }
        throw new SerializationException(node, type, "Bound must be expressed as array or map");
    }
}
