package com.mw.cotea_core.store

import com.mw.cotea_core.command_handler.CommandHandler
import com.mw.cotea_core.state_machine.StateMachine
import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.transition.TransitionListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Фабрика для создания [Store]
 * Создается с помощью метода [createStore]
 *
 * @see Store
 * @see StateMachine
 * @see StateUpdater
 * @see CommandHandler
 * @see TransitionListener
 */
object StoreFactory {

    /**
     * Создает [StateMachine] инициализируя его [StateUpdater] и [initialState]
     * Создает [StoreImpl], инициализируя его [StateMachine], [CommandHandler], [initialCommands]. [transitionListener]
     * и возвращает [Store] интерфейс для взаимодействия с обобщенной логикой.
     *
     * @param initialState начальное состояния стейт-машины
     * @param initialCommands начальные команды
     * @param stateUpdater редюсер состояния стейт-машины
     * @param commandHandler обработчик команд испускаемых стейт-машиной
     * @param transitionListener слушатель изменний состояния стейт-машины, а так же испускаемых команд и еффектов
     * @return [Store]
     */
    fun <Message, State, SideEffect, Command> createStore(
        stateUpdater: StateUpdater<Message, State, SideEffect, Command>,
        commandHandler: CommandHandler<Message, Command>,
        initialState: State,
        initialCommands: List<Command>,
        transitionListener: TransitionListener<Message, State, SideEffect, Command>? = null,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Store<Message, State, SideEffect, Command> {
        val stateMachine: StateMachine<Message, State, SideEffect, Command> = StateMachine(
            stateUpdater,
            initialState,
            coroutineDispatcher
        )

        return StoreImpl(
            stateMachine = stateMachine,
            commandHandler = commandHandler,
            initialCommands = initialCommands,
            transitionListener = transitionListener
        )
    }
}

