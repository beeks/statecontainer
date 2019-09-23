package com.example

import org.pcollections.*

/*
    DataTypes.kt - contains definitions for some standard data types along with helpers for creating them. See
    DataTypesFuns.kt for functions that manipulate them. We use typealiases instead of the pcollections interfaces
    directly so the potential for changing the implementation in the future can be as transparent to consumers as
    possible, as we (or a library consumer) can use extension methods to fill in anything that's missing or has been
    renamed by the new implementation. This is achieved without the use of wrapper interfaces/classes, which makes
    working with these structures a million times easier (not having to proxy methods through, make copies of the
    wrapper object, etc.)
 */

/**
 * Map to store data using Strings as keys. Immutable and persistent! Extension methods for adding type support include:
 * [getField], [plusField], [minusField], which all use [DataField]s to ensure type safety
 */
typealias DataMap = PMap<String, Any>
fun emptyDataMap(): DataMap = HashTreePMap.empty<String, Any>()

/**
 * Make sure when using [PVector.plus] on this class that the item being added is properly a type T or it will try to
 * use the Kotlin [Collection.plus] method instead
 */
typealias TypedVector<T> = PVector<T>
typealias TypedMap<K, V> = PMap<K, V>
typealias TypedSet<T> = PSet<T>
fun <T> emptyTypedVector(): TypedVector<T> = TreePVector.empty<T>()
fun <K, V> emptyTypedMap(): TypedMap<K, V> = HashTreePMap.empty<K, V>()
fun <T> emptyTypedSet(): TypedSet<T> = HashTreePSet.empty()

/**
 * Used as an extension to [DataMap]s by allowing the storing and retrieval of typed objects in an otherwise
 * typeless (String -> Any) map. The generic parameter specifies the object type and the name should be a String
 * that can serve as a unique key amongst the places the [DataMap] will be used. For example:
 *
 * val X_COORDINATE = DataField<Float>("com.example.Coordinates.X_COORDINATE"), and used like:
 *
 * dataMap.plusField(X_COORDINATE, 1.1f)
 */
@Suppress("unused")
data class DataField<T>(val name: String)

fun <T> DataMap.getField(dataField: DataField<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return get(dataField.name) as T?
}

fun <T> DataMap.plusField(dataField: DataField<T>, value: T): DataMap {
    return plus(dataField.name, value)
}

fun <T> DataMap.minusField(dataField: DataField<T>): DataMap {
    return minus(dataField.name)
}

fun DataMap.containsField(dataField: DataField<*>): Boolean = containsKey(dataField.name)

typealias DataVector = PVector<Any>
fun emptyDataList(): DataVector = TreePVector.empty()

/**
 * Create a [DataMap] by supplying mappings as an even number of arguments (key1, value1, key2, value2)
 *
 * Keys must be [String]s or [DataField]s. The latter will get placed in as a string
 */
fun dm(vararg args: Any): DataMap {
    if (args.size % 2 != 0) {
        throw IllegalArgumentException("tried creating map with an odd number arguments: ${args.size}")
    }
    val iterator = args.iterator()
    var map = emptyDataMap()
    while(iterator.hasNext()) {
        val key = iterator.next()
        map = map.plus(
                when(key) {
                    is String -> key
                    is DataField<*> -> key.name
                    else -> throw IllegalArgumentException()
                },
                iterator.next())
    }
    return map
}

/**
 * Create a [DataVector] from the arguments list
 */
fun dv(vararg args: Any): DataVector = args.fold(emptyDataList()) { acc, value -> acc.plus(value) }

/**
 * Creates a [TypedVector] from the arguments list
 */
fun <T> tv(vararg args: T): TypedVector<T> = args.fold(emptyTypedVector()) { acc, value -> acc.plus(value) }

