package com.example

import com.example.impl.StoreImpl

fun make(str: String): BasicAction {
    return object: BasicAction {
        override fun what(): String {
            return str
        }
    }
}

fun main() {
    val a = StoreImpl(emptyMap(), {
        a: BasicAction, state: Map<String, Any> -> state.plus(Pair(a.what(), "should be something"))
    })
    a.subscribe {
        a.getState().forEach(::println)
        println()
    }
    a.dispatch(make("action"))

    a.dispatch(make("action2"))
}
