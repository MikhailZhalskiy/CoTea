package com.mw.cotea_core.store

import kotlinx.coroutines.CoroutineScope

/**
 * Обобщенная логика над стейт-машиной [com.mw.cotea_core.state_machine.StateMachine]
 * и связанная с логикой обработки ее команд [com.mw.cotea_core.command_handler.CommandHandler],
 * реагирует на входящие события [onMessage].
 *
 * Связывание происходит в момент вызова метода [start],
 * через переданные callback [actionState] и [actionSideEffect] возвращается [State] и [SideEffect]
 */
interface Store<Message, State, SideEffect, Command>{

    suspend fun onMessage(message: Message)

    fun start(
        coroutineScope: CoroutineScope,
        actionState: suspend (State) -> Unit,
        actionSideEffect: suspend (SideEffect) -> Unit,
    )
}