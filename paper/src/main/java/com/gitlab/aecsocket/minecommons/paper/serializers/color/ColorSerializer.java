package com.gitlab.aecsocket.minecommons.paper.serializers.color;

import com.gitlab.aecsocket.minecommons.core.serializers.color.AbstractRGBSerializer;
import org.bukkit.Color;

/**
 * Type serializer for a {@link Color}
 * <p>
 * Uses the format of {@link AbstractRGBSerializer}.
 */
public class ColorSerializer extends AbstractRGBSerializer<Color> {
    /** A singleton instance of this serializer. */
    public static final ColorSerializer INSTANCE = new ColorSerializer(Format.COMPONENTS);

    /**
     * Creates an instance.
     * @param format The format to use.
     */
    public ColorSerializer(Format format) {
        super(format);
    }

    @Override protected int value(Color obj) { return obj.asRGB(); }
    @Override protected double r(Color obj) { return obj.getRed(); }
    @Override protected double g(Color obj) { return obj.getGreen(); }
    @Override protected double b(Color obj) { return obj.getBlue(); }

    @Override protected Color of(int value) { return Color.fromRGB(value); }
}
