package com.mw.cotea_core.transition

data class Transition<Message, State> internal constructor(
    val event: Message,
    val oldState: State,
    val newState: State,
)