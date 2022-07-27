package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.DoubleMod
import com.gitlab.aecsocket.alexandria.core.IntMod
import com.gitlab.aecsocket.alexandria.core.Quantifier
import com.gitlab.aecsocket.alexandria.core.effect.SoundEffect
import com.gitlab.aecsocket.alexandria.core.extension.register
import com.gitlab.aecsocket.alexandria.core.extension.registerExact
import com.gitlab.aecsocket.alexandria.core.input.InputMapper
import com.gitlab.aecsocket.alexandria.core.physics.*
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.util.*

object Serializers {
    val ALL: TypeSerializerCollection = TypeSerializerCollection.builder()
        .registerExact(Locale::class, LocaleSerializer)
        .registerExact(WDuration::class, DurationSerializer)
        .registerExact(Vector2::class, Vector2Serializer)
        .registerExact(Vector3::class, Vector3Serializer)
        .registerExact(Point2::class, Point2Serializer)
        .registerExact(Point3::class, Point3Serializer)
        .registerExact(SoundEffect::class, SoundEffectSerializer)
        .registerExact(DoubleMod::class, DoubleModSerializer)
        .registerExact(IntMod::class, IntModSerializer)
        .registerExact(Shape::class, ShapeSerializer)
        .registerExact(Transform::class, TransformSerializer)
        .registerExact(SimpleBody::class, SimpleBodySerializer)
        .registerExact(InputMapper::class, InputMapperSerializer)
        .register(Quantifier::class, QuantifierSerializer)
        .build()
}
