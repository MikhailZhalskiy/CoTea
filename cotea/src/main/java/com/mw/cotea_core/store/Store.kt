package com.mw.cotea_core.store

import kotlinx.coroutines.CoroutineScope

// TODO: возможно interface не нужен
interface Store<Message, State, SideEffect, Command>{

    suspend fun onMessage(message: Message)

    fun start(
        coroutineScope: CoroutineScope,
        actionState: suspend (State) -> Unit,
        actionSideEffect: suspend (SideEffect) -> Unit,
    )
}