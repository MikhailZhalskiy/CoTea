package com.mw.cotea_core.state_updater.dsl

import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.state_updater.Update

abstract class StateUpdaterDsl<Message, State, SideEffect, Command> :
    StateUpdater<Message, State, SideEffect, Command> {

    private var sideEffects = mutableListOf<SideEffect>()
    private var commands = mutableListOf<Command>()
    private var states = mutableListOf<State.() -> State>()

    protected abstract fun updateDsl(state: State, message: Message)

    final override fun update(state: State, message: Message): Update<State, SideEffect, Command> {
        updateDsl(state, message)
        return build(state)
    }

    protected fun sideEffect(sideEffect: () -> SideEffect) {
        sideEffects += sideEffect()
    }

    protected fun command(command: () -> Command) {
        commands += command()
    }

    protected fun updateSate(newState: State.() -> State) {
        states += newState
    }

    private fun build(initialState: State): Update<State, SideEffect, Command> {
        val newUpdate = Update.stateWithSideEffectsWithCommands(
            state = if (states.isNotEmpty()) states.fold(initialState) { state, updateState -> state.updateState() } else null,
            sideEffects = sideEffects,
            commands = commands
        )
        sideEffects = mutableListOf()
        commands = mutableListOf()
        states = mutableListOf()
        return newUpdate
    }
}