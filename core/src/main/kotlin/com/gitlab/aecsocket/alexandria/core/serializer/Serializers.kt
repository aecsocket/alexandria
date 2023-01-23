package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.extension.register
import com.gitlab.aecsocket.alexandria.core.extension.registerExact
import org.spongepowered.configurate.serialize.TypeSerializerCollection

object Serializers {
    val All: TypeSerializerCollection = TypeSerializerCollection.builder()
        .registerExact(LocaleSerializer)
        .registerExact(LogLevelSerializer)
        .registerExact(DurationSerializer)
        .registerExact(Vector2Serializer)
        .registerExact(Vector3Serializer)
        .registerExact(Point2Serializer)
        .registerExact(Point3Serializer)
        .registerExact(QuaternionSerializer)
        .registerExact(ColorModSerializer)
        .registerExact(EmptyShapeSerializer)
        .registerExact(ShapeSerializer)
        .registerExact(RangeMapFloatSerializer)
        .registerExact(RangeMapDoubleSerializer)
        .register(QuantifierSerializer)
        .register(InputMapperSerializer)
        .build()
}
