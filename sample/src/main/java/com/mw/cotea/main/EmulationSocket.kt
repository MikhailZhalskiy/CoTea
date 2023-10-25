package com.mw.cotea.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EmulationSocket(
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO
) : CoroutineScope {
    private val sharedFlow = MutableSharedFlow<Int>()
    private var jobSocket: Job? = null

    fun getSocketFlow(): Flow<Int> = sharedFlow

    fun start() {
        if (jobSocket == null) {
            jobSocket = launch {
                var i = 0
                while (isActive) {
                    sharedFlow.emit(i++)
                    delay(500)
                }
            }
        }
    }

    fun stop() {
        if (jobSocket != null) {
            jobSocket?.cancel()
            jobSocket = null
        }
    }
}