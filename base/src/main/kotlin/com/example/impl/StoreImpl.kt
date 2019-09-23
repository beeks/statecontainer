package com.example.impl

import com.example.api.Middleware
import com.example.api.Store

class StoreImpl<Action, State> constructor(initialState: State, reducer: (Action, State) -> State):
        Store<Action, State> {
    private val reducer: (Action, State) -> State
    private var entryDispatch: (Action) -> Any
    private var currentState = initialState
    private var listeners = LinkedHashSet<() -> Unit>()

/**
    When initially processing the list of Middleware, this constructor will call [Middleware.make], call the returned
    function with the basic store, and call that returned function with the "next" pointer; thus, the final function
    returned (with signature (Action -> Any)) will be the only reference used by this [StoreImpl] for the course of its
    life. That is to say, attempting to return different function implementations for [Middleware.make] or the other
    two won't work because they are only ever called once during this constructor. This also means you are free to use
    lambdas for all the functions and not worry about the inefficiency that would result from them being created over
    and over
 */
    constructor(initialState: State, reducer: (Action, State) -> State, middles: List<Middleware<Action, State>>):
            this(initialState, reducer) {
        val basicStore = object : Middleware.BasicStore<Action, State> {
            override fun dispatch(action: Action): Any {
                // Every new action goes through the full gauntlet of middleware, and the listeners will be called
                return this@StoreImpl.dispatch(action)
            }

            override fun getState(): State {
                return this@StoreImpl.getState()
            }
        }

        var dispatch: (Action) -> Any  = this::storeDispatch
        middles.reversed().forEach { middle ->
            dispatch = middle.make()(basicStore)(dispatch)
        }

        entryDispatch = dispatch
    }

    init {
        entryDispatch = this::storeDispatch
        this.reducer = reducer
    }

    override fun dispatch(action: Action): Any {
        val result = entryDispatch(action)
        listeners.forEach({
            it.invoke()
        })
        return result
    }

    private fun storeDispatch(action: Action) {
        currentState = reducer.invoke(action, getState())
    }

    override fun getState(): State {
        return currentState
    }

    override fun subscribe(onUpdate: () -> Unit): () -> Unit {
        listeners.add(onUpdate)
        return {
            listeners.remove(onUpdate)
            Unit
        }
    }
}