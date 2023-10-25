package com.mw.cotea.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext

class SingleShotEvent<Event> {
    private val _events = Channel<Event>()
    val events = _events.receiveAsFlow() // expose as flow

    suspend fun postEvent(event: Event) {
        withContext(Dispatchers.Main.immediate) {
            _events.send(event) // suspends on buffer overflow
        }
    }
}