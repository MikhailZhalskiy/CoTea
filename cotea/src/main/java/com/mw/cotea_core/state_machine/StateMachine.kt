package com.mw.cotea_core.state_machine

import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.transition.Transition
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.scan

/**
 * Стейт-машина связывающая логику [StateUpdater] и передачу данных по каналам
 * для команд и сайд-эффектов и нового состояния [State].
 *
 * Реагирует на внешние события [Message] приходящие в метод [onMessage],
 * результатом реакции на событие будет новое состояние проброшенное дальше по flow
 * набор сайд-эффектов брошенных в [sideEffectsSharedFlow].
 * набор команд брошенных в [commandsSharedFlow].
 * через канал [transitionSharedFlow] будет проброшен [Transition]
 * Если состояние не изменилось или в результате реакции на событие не было создано никаких сайд-эффектов
 * и/или команд то соостветвующие источники ничего не излучают.
 *
 * @property messageSharedFlow наблюдаемый источник входящик событий
 * @property sideEffectsSharedFlow наблюдаемый источник излучаемых сайд-эффектов
 * @property commandsSharedFlow наблюдаемый источник излучаемых команд
 * @property transitionSharedFlow наблюдаемый источник излучаемых [Transition]
 */
class StateMachine<Message, State, SideEffect, Command>(
    private val stateUpdater: StateUpdater<Message, State, SideEffect, Command>,
    private val initialState: State,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    private val messageSharedFlow = MutableSharedFlow<Message>(extraBufferCapacity = Int.MAX_VALUE)
    private val commandsSharedFlow = MutableSharedFlow<List<Command>>(extraBufferCapacity = Int.MAX_VALUE)
    private val sideEffectsSharedFlow = MutableSharedFlow<List<SideEffect>>(extraBufferCapacity = Int.MAX_VALUE)
    private val transitionSharedFlow = MutableSharedFlow<Transition<Message, State, SideEffect, Command>>(extraBufferCapacity = Int.MAX_VALUE)

    suspend fun onMessage(message: Message) {
        messageSharedFlow.emit(message)
    }

    fun getStateSource(onSubscription: suspend () -> Unit): Flow<State> {
        return messageSharedFlow
            .onSubscription { onSubscription() }
            .scan(initialState) { state, message ->
            val (updatedState, sideEffects, commands) = stateUpdater.update(state, message)
            commands?.let { commandsSharedFlow.emit(it) }
            sideEffects?.let { sideEffectsSharedFlow.emit(it) }
            sendTransition(state, message, updatedState, sideEffects, commands)
            updatedState ?: state
        }
            .distinctUntilChanged { oldState, newState -> oldState === newState }
            .flowOn(coroutineDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSideEffectSource(onSubscription: () -> Unit): Flow<SideEffect> {
        return sideEffectsSharedFlow
            .onSubscription { onSubscription() }
            .flatMapMerge { it.asFlow().cancellable() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCommandSource(onSubscription: () -> Unit): Flow<Command> {
        return commandsSharedFlow
            .onSubscription { onSubscription() }
            .flatMapMerge { it.asFlow().cancellable() }
    }

    fun getTransitionSource(onSubscription: () -> Unit): Flow<Transition<Message, State, SideEffect, Command>> {
        return transitionSharedFlow
            .onSubscription { onSubscription() }
    }

    suspend fun emitInitialCommands(initialCommands: List<Command>) {
        commandsSharedFlow.emit(initialCommands)
    }

    private suspend fun sendTransition(state: State, message: Message, newState: State?, sideEffects: List<SideEffect>?, commands: List<Command>?) {
        val transition = Transition(
            state = state,
            message = message,
            updatedState = newState ?: state,
            sideEffects = sideEffects.orEmpty(),
            commands = commands.orEmpty()
        )
        transitionSharedFlow.emit(transition)
    }
}