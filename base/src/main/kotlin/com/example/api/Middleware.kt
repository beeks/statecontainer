package com.example.api

interface Middleware<Action, State> {
    interface BasicStore<Action, State> {
        fun dispatch(action: Action): Any
        fun getState() : State
    }

    fun make(): (BasicStore<Action, State>) -> (next: (Action) -> Any) -> (Action) -> Any
}