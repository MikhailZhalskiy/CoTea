package com.mw.cotea_core.store

import com.mw.cotea_core.command_handler.CommandHandler
import com.mw.cotea_core.state_machine.StateMachine
import com.mw.cotea_core.transition.TransitionListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class StoreImpl<Message, State, SideEffect, Command>(
    private val stateMachine: StateMachine<Message, State, SideEffect, Command>,
    private val commandHandler: CommandHandler<Message, Command>,
    private val initialCommands: List<Command>,
    private val transitionListener: TransitionListener<Message, State, SideEffect, Command>? = null
): Store<Message, State, SideEffect, Command> {

    private val transitionSourceStarted = CompletableDeferred<Unit>()
    private val stateSourceStarted = CompletableDeferred<Unit>()
    private val commandSourceStarted = CompletableDeferred<Unit>()
    private val sideEffectSourceStarted = CompletableDeferred<Unit>()
    private val commandMessageSourceStarted = CompletableDeferred<Unit>()

    override suspend fun onMessage(message: Message) {
        stateSourceStarted.join()
        stateMachine.onMessage(message)
    }

    override fun start(
        coroutineScope: CoroutineScope,
        actionState: suspend (State) -> Unit,
        actionSideEffect: suspend (SideEffect) -> Unit,
    ) {
        if (transitionListener != null) {
            stateMachine.getTransitionSource()
                .onStart {
                    transitionSourceStarted.complete(Unit)
                }
                .onEach { transitionListener.onTransition(it) }
                .launchIn(coroutineScope)
        } else {
            transitionSourceStarted.complete(Unit)
        }

        stateMachine.getSideEffectSource()
            .onStart {
                sideEffectSourceStarted.complete(Unit)
            }
            .onEach(actionSideEffect)
            .launchIn(coroutineScope)

        commandHandler.getMessageSource()
            .onStart {
                commandMessageSourceStarted.complete(Unit)
            }
            .onEach(stateMachine::onMessage)
            .launchIn(coroutineScope)

        stateMachine.getCommandSource()
            .onStart {
                commandSourceStarted.complete(Unit)
            }
            .onEach(commandHandler::onCommand)
            .launchIn(coroutineScope)

        stateMachine.getStateSource()
            .onStart {
                transitionSourceStarted.join()
                sideEffectSourceStarted.join()
                commandMessageSourceStarted.join()
                commandSourceStarted.join()
                stateSourceStarted.complete(Unit)
            }
            .onEach(actionState)
            .launchIn(coroutineScope)

        coroutineScope.launch {
            stateSourceStarted.join()
            stateMachine.emitInitialCommands(initialCommands)
        }
    }
}