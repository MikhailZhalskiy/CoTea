package com.mw.cotea_core.store

import com.mw.cotea_core.command_handler.CommandHandler
import com.mw.cotea_core.state_machine.StateMachine
import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.transition.TransitionListener

object StoreFactory {

    fun <Message, State, SideEffect, Command> createStore(
        stateUpdater: StateUpdater<Message, State, SideEffect, Command>,
        commandHandler: CommandHandler<Message, Command>,
        initialState: State,
        initialCommands: List<Command>,
        transitionListener: TransitionListener<Message, State>? = null
    ): Store<Message, State, SideEffect, Command> {
        val stateMachine: StateMachine<Message, State, SideEffect, Command> = StateMachine(
            stateUpdater,
            initialState,
        )

        return StoreImpl(
            stateMachine = stateMachine,
            commandHandler = commandHandler,
            initialCommands = initialCommands,
            transitionListener = transitionListener
        )
    }
}

