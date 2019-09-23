package com.example

/*
    DataTypesFuns.kt - defines convenience functions that extend the types defined in DataTypes.kt. As extension
    functions of typealiases, they have the highest likelihood of being consumer-transparent if we decide to switch
    to a different persistent data structure library later on, without requiring a class or interface wrapper/proxy.
 */

/**
 * Returns [null] if a) the innermost key has no associated value, or b) the "this" object does not have a map nesting
 * that matches [vecs]
 */
fun DataMap.getIn(vecs: DataVector): Any? {
    var map = this
    try {
        val end = vecs.size - 1
        vecs.forEachIndexed { index, key ->
            if (index == end) {
                return map.get(key)
            }
            map = map.get(key) as? DataMap ?: return null
        }
    } catch(e: ClassCastException) {
        BLog.e("DataMap.getIn", "ClassCastException: " + e)
        return null
    }
    return null
}

fun DataMap.assocIn(vecs: DataVector, value: Any): DataMap {
    return assocInHelper(this, vecs, value)
}

fun <T> Collection<T>.toDataVector(): DataVector {
    return this.fold(emptyDataList()) { accum, value ->
        accum.plus(value)
    }
}

/**
 * Favors map2 value unless both values are Vectors, in which case map2's vector gets added to the end of map1's vector
 */
val defaultMerger = { map1Value: Any, map2Value: Any ->
    when {
        map1Value is TypedVector<*> && map2Value is TypedVector<*> -> (map2Value).fold(map1Value as
                TypedVector<Any>) { accum, value ->
            accum.plus(value)
        }
        else -> map2Value
    }
}

fun DataMap.mergeWith(other: DataMap, merger: (map1Value: Any, map2Value: Any) -> Any = defaultMerger): DataMap {
    return mergeHelper(this, other, merger)
}

private fun assocInHelper(map: DataMap, vecs: DataVector, value: Any): DataMap {
    return when {
        vecs.size == 0 -> {
            throw IllegalArgumentException()
        }
        vecs.size == 1 -> {
            map.plus(vecs.get(0) as String, value)
        }
        else -> {
            // Call recursively to get the inner map, then add the result to the one on this level
            assocInHelper(map.get(vecs.get(0) as String) as DataMap?
                    ?: emptyDataMap(), vecs.minus(0), value).let {
                map.plus(vecs.get(0) as String, it)
            }
        }
    }
}

private fun mergeHelper(map1: DataMap, map2: DataMap, merger: (map1Value: Any, map2Value: Any) -> Any): DataMap {
    val map2KeySet = map2.keys
    val map1ConflictsResolved = map1.keys.fold(map1) { accum, key ->
        map2KeySet.remove(key)
        val map1Value = map1.get(key)
        val map2Value = map2.get(key)
        @Suppress("UNCHECKED_CAST") // oh you silly type systems...
        val valToInsert: Any? = when {
            map1Value == null -> null // should never happen because DataMaps should not store null values
            map2Value == null -> null // this is the case that map1Value has no counterpart, so don't insert anything
            map1Value is TypedMap<*, *> && map2Value is TypedMap<*, *> -> mergeHelper(map1Value as DataMap, map2Value
                    as DataMap, merger)
            map2Value != map1Value -> merger(map1Value, map2Value)
            else -> null // they should be == at this point, so don't bother re-adding map1Value to map1
        }
        valToInsert?.let {
            accum.plus(key, it)
        } ?: accum
    }
    // now that we've seen everything map1 has to offer, add the rest of the uncontested keys & values from map2
    return map2KeySet.fold(map1ConflictsResolved) { accum, key ->
        accum.plus(key, map2.get(key)!!)
    }
}

