package com.mw.cotea_core.command_handler

import kotlinx.coroutines.flow.Flow

interface CommandHandler<Message, Command> {

    fun getMessageSource(): Flow<Message>

    suspend fun onCommand(command: Command)
}