package com.github.aecsocket.alexandria.core.serializer

import com.github.aecsocket.alexandria.core.DoubleMod
import com.github.aecsocket.alexandria.core.IntMod
import com.github.aecsocket.alexandria.core.effect.SoundEffect
import com.github.aecsocket.alexandria.core.extension.register
import com.github.aecsocket.alexandria.core.extension.registerExact
import com.github.aecsocket.alexandria.core.vector.Point2
import com.github.aecsocket.alexandria.core.vector.Point3
import com.github.aecsocket.alexandria.core.vector.Vector2
import com.github.aecsocket.alexandria.core.vector.Vector3
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.util.Locale
import kotlin.time.Duration

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
        .build()
}
