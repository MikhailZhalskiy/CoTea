package com.mw.cotea_core.state_updater

class Update<State, SideEffect, Command> internal constructor(
    val state: State?,
    val sideEffects: List<SideEffect>?,
    val commands: List<Command>?
) {
    operator fun component1(): State? {
        return state
    }

    operator fun component2(): List<SideEffect>? {
        return sideEffects
    }

    operator fun component3(): List<Command>? {
        return commands
    }

    companion object {
        fun <State, SideEffect, Command> nothing(): Update<State, SideEffect, Command> = Update(null, null, null)

        fun <State, SideEffect, Command> state(state: State) = Update<State, SideEffect, Command>(state = state, sideEffects = null, commands = null)

        fun <State, SideEffect, Command> sideEffects(vararg sideEffects: SideEffect) = Update<State, SideEffect, Command>(state = null, sideEffects = sideEffects.ifEmpty { null }?.toList(), commands = null)

        fun <State, SideEffect, Command> sideEffects(sideEffects: List<SideEffect>) = Update<State, SideEffect, Command>(state = null, sideEffects = sideEffects, commands = null)

        fun <State, SideEffect, Command> commands(vararg commands: Command) = Update<State, SideEffect, Command>(state = null, sideEffects = null, commands = commands.ifEmpty { null }?.toList())

        fun <State, SideEffect, Command> commands(commands: List<Command>) = Update<State, SideEffect, Command>(state = null, sideEffects = null, commands = commands)

        fun <State, SideEffect, Command> stateWithSideEffectsWithCommands(state: State? = null, sideEffects: List<SideEffect>? = null, commands: List<Command>? = null) = Update(state = state, sideEffects = sideEffects, commands = commands)

    }
}
