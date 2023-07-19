package com.mw.cotea_core.command_handler

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapMerge

/**
 * Дефолтная реализация обработчика команд [Command]
 * испускаемых [стейт-машиной][ru.broker.my.mvi.state_machine.StateMachine].
 *
 * В унаследованном классе необходимо реализовать метод [execute]
 * который будет содержать when (command) { } возвращающий [Message]
 */
abstract class CommandHandlerDefault<Message, Command>(): CommandHandler<Message, Command> {

    private val commandSharedFlow = MutableSharedFlow<Command>(Int.MAX_VALUE)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMessageSource(): Flow<Message> {
        return commandSharedFlow.flatMapMerge(transform = ::execute)
    }

    override suspend fun onCommand(command: Command) {
        commandSharedFlow.emit(command)
    }

    abstract suspend fun execute(command: Command): Flow<Message>
}