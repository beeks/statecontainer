package com.example

val DELETED_KEYS = DataField<DataVector>("com.example.Diff.DELETED_KEYS")

/**
 * Returns a function that takes a [DataMap] and tracks the differences from call to call, outputting the new map
 * with only the differences, as well as a [DELETED_KEYS] vector containing any that were removed
 */
fun trackDiffs(fieldAddresses: DataVector): (DataMap) -> DataMap {
    val prevValues = MutableList<Any?>(fieldAddresses.size) { null }
    fun DataVector.getLast() = get(size - 1)
    return { state ->
        val newState = prevValues.foldIndexed(emptyDataMap()) { index, collector, prevValue ->
            val fieldAddressIndexed = fieldAddresses.get(index) as DataVector
            val newValue = state.getIn(fieldAddressIndexed)
            // new = 1, prev = null
            // new = null, prev = 1
            // new = 2, prev = 1

            if (newValue != prevValue) {
                prevValues[index] = newValue

                val finalNodeInPath = fieldAddressIndexed.getLast()
                val fieldName = finalPathNode(finalNodeInPath)

                if (newValue != null) {
                    collector.plus(fieldName, newValue)
                } else {
                    val mergeMe = dm(DELETED_KEYS, dv(fieldName))
                    collector.mergeWith(mergeMe)
                }
            }
            // new = 1, prev = 1
            // new = null, prev = null
            else {
                collector
            }
        }
        newState
    }
}
