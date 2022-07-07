package com.github.aecsocket.alexandria.core.serializer

import com.github.aecsocket.alexandria.core.DoubleMod
import com.github.aecsocket.alexandria.core.IntMod
import com.github.aecsocket.alexandria.core.bound.Bound
import com.github.aecsocket.alexandria.core.effect.SoundEffect
import com.github.aecsocket.alexandria.core.extension.register
import com.github.aecsocket.alexandria.core.extension.registerExact
import com.github.aecsocket.alexandria.core.spatial.Point2
import com.github.aecsocket.alexandria.core.spatial.Point3
import com.github.aecsocket.alexandria.core.spatial.Vector2
import com.github.aecsocket.alexandria.core.spatial.Vector3
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.util.Locale

object Serializers {
    val ALL: TypeSerializerCollection = TypeSerializerCollection.builder()
        .register(Locale::class, LocaleSerializer)
        .register(WDuration::class, DurationSerializer)
        .register(Vector2::class, Vector2Serializer)
        .register(Vector3::class, Vector3Serializer)
        .register(Point2::class, Point2Serializer)
        .register(Point3::class, Point3Serializer)
        .register(SoundEffect::class, SoundEffectSerializer)
        .registerExact(DoubleMod::class, DoubleModSerializer)
        .registerExact(IntMod::class, IntModSerializer)
        .registerExact(Bound::class, BoundSerializer)
        .build()
}
