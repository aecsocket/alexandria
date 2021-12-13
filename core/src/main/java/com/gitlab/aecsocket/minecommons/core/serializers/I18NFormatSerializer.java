package com.gitlab.aecsocket.minecommons.core.serializers;

import com.gitlab.aecsocket.minecommons.core.i18n.Format;
import com.gitlab.aecsocket.minecommons.core.i18n.I18N;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.format.Style;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import static com.gitlab.aecsocket.minecommons.core.serializers.Serializers.require;

/**
 * Type serializer for a {@link Format}.
 */
public class I18NFormatSerializer implements TypeSerializer<Format> {
    /** A singleton instance of this serializer. */
    public static final I18NFormatSerializer INSTANCE = new I18NFormatSerializer();

    @Override
    public void serialize(Type type, @Nullable Format obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.appendListNode().set(obj.style());
            node.appendListNode().set(obj.templates());
        }
    }

    @Override
    public Format deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!node.isList())
            throw new SerializationException(node, type, "Node must be list of [optional style, optional templates]");
        var list = node.childrenList();
        ConfigurationNode style = node.node(0);
        return I18N.format(
                style.empty() ? null : style.get(Style.class),
                node.node(1).get(new TypeToken<Map<String, Style>>() {}, Collections.emptyMap())
        );
    }
}
