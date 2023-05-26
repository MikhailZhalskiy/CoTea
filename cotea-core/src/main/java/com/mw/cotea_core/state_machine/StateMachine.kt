package com.mw.cotea_core.state_machine

import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.transition.Transition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.scan

class StateMachine<Message, State, SideEffect, Command>(
    private val stateUpdater: StateUpdater<Message, State, SideEffect, Command>,
    private val initialState: State,
) {

    private val commandsSharedFlow = MutableSharedFlow<List<Command>>()
    private val sideEffectsSharedFlow = MutableSharedFlow<List<SideEffect>>()
    private val transitionSharedFlow = MutableSharedFlow<Transition<Message, State>>(extraBufferCapacity = Int.MAX_VALUE)

    fun getStateSource(messageSource: Flow<Message>): Flow<State> {
        return messageSource.scan(initialState) { state, Message ->
            val (newState, effects, commands) = stateUpdater.update(Message, state)
            commands?.let { commandsSharedFlow.emit(it) }
            effects?.let { sideEffectsSharedFlow.emit(it) }
            sendTransition(Message, state, newState)
            newState ?: state
        }
            .distinctUntilChanged { oldState, newState -> oldState === newState }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSideEffectSource(): Flow<SideEffect> {
        return sideEffectsSharedFlow
            .flatMapMerge { it.asFlow().cancellable() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCommandSource(): Flow<Command> {
        return commandsSharedFlow
            .flatMapMerge { it.asFlow().cancellable() }
    }

    fun getTransitionSource(): Flow<Transition<Message, State>> {
        return transitionSharedFlow
    }

    suspend fun emitInitialCommands(initialCommands: List<Command>) {
        commandsSharedFlow.emit(initialCommands)
    }

    private suspend fun sendTransition(message: Message, state: State, newState: State?) {
        val transition = Transition(message, state, newState ?: state)
        transitionSharedFlow.emit(transition)
    }
}