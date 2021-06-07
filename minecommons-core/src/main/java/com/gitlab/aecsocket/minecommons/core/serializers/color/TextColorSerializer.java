package com.gitlab.aecsocket.minecommons.core.serializers.color;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link TextColor}
 * <p>
 * Uses the format of {@link AbstractRGBSerializer}.
 * Can also deserialize {@link NamedTextColor}s.
 */
public class TextColorSerializer extends AbstractRGBSerializer<TextColor> {
    public static final TextColorSerializer INSTANCE = new TextColorSerializer(Format.COMPONENTS);

    public TextColorSerializer(Format format) {
        super(format);
    }

    @Override protected int value(TextColor obj) { return obj.value(); }
    @Override protected double r(TextColor obj) { return obj.red(); }
    @Override protected double g(TextColor obj) { return obj.green(); }
    @Override protected double b(TextColor obj) { return obj.blue(); }

    @Override protected TextColor of(int value) { return TextColor.color(value); }

    @Override
    protected TextColor fallback(Type type, ConfigurationNode node) throws SerializationException {
        String key = node.getString();
        if (key != null) {
            NamedTextColor color = NamedTextColor.NAMES.value(key);
            if (color != null)
                return color;
        }
        return super.fallback(type, node);
    }
}
