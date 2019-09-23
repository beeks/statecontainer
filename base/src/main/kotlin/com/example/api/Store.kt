package com.example.api

interface Store<Action, State> {
    /**
     * Send an action into the store to be processed first by [Middleware], then by
     */
    fun dispatch(action: Action): Any
    /**
     * Synchronously grab the present state
     */
    fun getState() : State
    /**
     * onUpdate() will get called for every dispatch call that doesn't otherwise get intercepted and dropped by a
     * [Middleware]
     */
    fun subscribe(onUpdate: () -> Unit): () -> Unit
}
