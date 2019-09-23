package com.example.impl

import com.example.api.Middleware

abstract class SimpleMiddleware<Action, State>: Middleware<Action, State> {
    override fun make(): (Middleware.BasicStore<Action, State>) -> (next: (Action) -> Any) -> (Action) -> Any {
        return { store ->
            { next ->
                { action ->
                    apply(action, store)
                    next(action)
                }
            }
        }
    }

    abstract fun apply(action: Action, basicStore: Middleware.BasicStore<Action, State>)
}