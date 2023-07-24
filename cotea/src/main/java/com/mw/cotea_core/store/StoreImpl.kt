package com.mw.cotea_core.store

import com.mw.cotea_core.command_handler.CommandHandler
import com.mw.cotea_core.state_machine.StateMachine
import com.mw.cotea_core.transition.TransitionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class StoreImpl<Message, State, SideEffect, Command>(
    private val stateMachine: StateMachine<Message, State, SideEffect, Command>,
    private val commandHandler: CommandHandler<Message, Command>,
    private val initialCommands: List<Command>,
    private val transitionListener: TransitionListener<Message, State, SideEffect, Command>? = null
): Store<Message, State, SideEffect, Command> {

    override suspend fun onMessage(message: Message) {
        stateMachine.onMessage(message)
    }

    override fun start(
        coroutineScope: CoroutineScope,
        actionState: suspend (State) -> Unit,
        actionSideEffect: suspend (SideEffect) -> Unit,
    ) {
        stateMachine.getStateSource()
            .onEach(actionState)
            .launchIn(coroutineScope)

        stateMachine.getSideEffectSource()
            .onEach(actionSideEffect)
            .launchIn(coroutineScope)

        stateMachine.getCommandSource()
            .onEach(commandHandler::onCommand)
            .launchIn(coroutineScope)

        commandHandler.getMessageSource()
            .onEach(stateMachine::onMessage)
            .launchIn(coroutineScope)

        if (transitionListener != null) {
            stateMachine.getTransitionSource()
                .onEach { transitionListener.onTransition(it) }
                .launchIn(coroutineScope)
        }

        coroutineScope.launch {
            stateMachine.emitInitialCommands(initialCommands)
        }
    }
}