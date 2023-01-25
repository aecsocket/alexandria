package io.gitlab.aecsocket.alexandria.paper.datatype

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

abstract class TagArray<T : Any>(
    private val backing: PersistentDataType<PersistentDataContainer, T>
) : PersistentDataType<Array<PersistentDataContainer>, Array<T>> {
    override fun getPrimitiveType() = Array<PersistentDataContainer>::class.java

    protected abstract fun fromPrimitiveList(values: List<T>): Array<T>

    override fun toPrimitive(
        obj: Array<T>,
        ctx: PersistentDataAdapterContext
    ) = obj.map { backing.toPrimitive(it, ctx) }.toTypedArray()

    override fun fromPrimitive(
        pdcs: Array<PersistentDataContainer>,
        ctx: PersistentDataAdapterContext
    ) = fromPrimitiveList(pdcs.map { backing.fromPrimitive(it, ctx) })
}

inline fun <reified T : Any> tagArrayDataTypeOf(backing: PersistentDataType<PersistentDataContainer, T>) = object : TagArray<T>(backing) {
    override fun getComplexType() = Array<T>::class.java
    override fun fromPrimitiveList(values: List<T>) = values.toTypedArray()
}
