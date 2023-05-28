package com.mw.cotea_core.transition

data class Transition<Message, State> internal constructor(
    val state: State,
    val message: Message,
    val newState: State,
)