package io.github.aecsocket.alexandria.serializer

import io.github.aecsocket.alexandria.extension.force
import java.lang.reflect.Type
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection

const val TYPE_KEY = "type"

/**
 * A serializer which will delegate (de)serialization to a serializer for a subtype of [T] based on
 * a string property in a node.
 *
 * Instances can be constructed either using the default constructor (passing in a map directly) or
 * using a simple DSL model to define subtypes ([Model]). If unspecified, the default key of the
 * type node is [TYPE_KEY].
 *
 * **This serializer must be added through [TypeSerializerCollection.Builder.registerExact] instead
 * of `register`!**
 *
 * Callers must provide a map of strings to subtypes with which nodes will be deserialized:
 * ```
 * interface Base
 * class Foo(val a: Int) : Base
 * class Bar(val b: String) : Base
 *
 * HierarchySerializer<Base>( // `<Base>` can be excluded
 *   mapOf(
 *     "foo" to Foo::class.java,
 *     "bar" to Bar::class.java,
 *   ),
 * )
 * // or
 * HierarchySerializer<Base> {
 *   // use `_` to omit the `Base` type parameter
 *   subType<_, Foo>("foo")
 *   subType<_, Bar>("bar")
 * }
 * ```
 *
 * Assuming a structure like:
 * ```
 * data class Root(
 *   val items: ArrayList<Base>,
 * )
 * ```
 *
 * With the [typeKey] as [TYPE_KEY], a possible serialized form would be:
 * ```json
 * {
 *   "items": [
 *     {
 *       "type": "foo",
 *       "a": 3
 *     },
 *     {
 *       "type": "bar",
 *       "b": "hello"
 *     }
 *   ]
 * }
 * ```
 */
class HierarchySerializer<T : Any>(
    private val subTypes: Map<String, Class<out T>>,
    private val typeKey: String = TYPE_KEY,
) : TypeSerializer<T> {
  constructor(
      typeKey: String = TYPE_KEY,
      block: Model<T>.() -> Unit,
  ) : this(block.subTypes(), typeKey)

  private val subTypeToKey = subTypes.map { (a, b) -> b to a }.associate { it }

  override fun serialize(type: Type, obj: T?, node: ConfigurationNode) {
    if (obj == null) node.set(null)
    else {
      val subType = obj::class.java
      val key =
          subTypeToKey[subType]
              ?: throw SerializationException(node, type, "$subType does not have a registered key")
      node.set(obj)
      if (!node.isMap)
          throw SerializationException(node, type, "$subType must be serialized as a map")
      node.node(typeKey).set(key)
    }
  }

  override fun deserialize(type: Type, node: ConfigurationNode): T {
    val typeName = node.node(typeKey).force<String>()
    val targetType =
        subTypes[typeName]
            ?: throw SerializationException(node, type, "Invalid type name '$typeName'")
    return node.force(targetType)
  }

  interface Model<T : Any> {
    fun subType(key: String, subType: Class<out T>)
  }
}

inline fun <T : Any, reified U : T> HierarchySerializer.Model<T>.subType(key: String) =
    subType(key, U::class.java)

private fun <T : Any> (HierarchySerializer.Model<T>.() -> Unit).subTypes():
    Map<String, Class<out T>> {
  val res = HashMap<String, Class<out T>>()
  this(
      object : HierarchySerializer.Model<T> {
        override fun subType(key: String, subType: Class<out T>) {
          if (res.contains(key))
              throw IllegalArgumentException("Subtype already exists for key $key")
          res[key] = subType
        }
      })
  return res
}
