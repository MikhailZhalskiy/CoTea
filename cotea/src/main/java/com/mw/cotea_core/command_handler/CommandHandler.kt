package com.mw.cotea_core.command_handler

import kotlinx.coroutines.flow.Flow

/**
 * Обработчик команд [Command]
 * испускаемых стейт-машиной [StateMachine].
 *
 * Получает команды испускаемые стейт-машиной в [onCommand] и должен их обработать.
 * Результатом обработки команды может быть новое событие (или множество),
 * которые направляются на вход в стейт-машину.
 * Обработка команд может завершиться и без каких-либо новых событий.
 *
 * Команды могут обрабатываться ассинхронно, результаты обработки
 * будут попадать в Flow в любом порядке.
 *
 *  [getMessageSource] Flow результатов обработки команд
 */
interface CommandHandler<Message, Command> {

    fun getMessageSource(): Flow<Message>

    suspend fun onCommand(command: Command)
}