package com.gitlab.aecsocket.alexandria.paper.datatype

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType

abstract class EnumDataType<T : Enum<T>> : PersistentDataType<String, T> {
    override fun getPrimitiveType() = String::class.java

    override fun toPrimitive(
        obj: T,
        ctx: PersistentDataAdapterContext
    ) = obj.name
}

inline fun <reified T : Enum<T>> enumDataTypeOf() = object : EnumDataType<T>() {
    override fun getComplexType() = T::class.java

    override fun fromPrimitive(
        name: String,
        ctx: PersistentDataAdapterContext
    ) = enumValueOf<T>(name)
}
