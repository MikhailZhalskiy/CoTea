package com.mw.cotea.main

import android.util.Log
import com.mw.cotea.util.asResource
import com.mw.cotea_core.command_handler.CommandHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class MainCommandHandler(
    private val emulationSocket: EmulationSocket = EmulationSocket(),
): CommandHandler<MainMessage, MainCommand> {

    private val commandSharedFlow = MutableSharedFlow<MainCommand>(Int.MAX_VALUE)

    override suspend fun onCommand(command: MainCommand) {
        commandSharedFlow.emit(command)
    }

    override fun getMessageSource(): Flow<MainMessage> {
        return merge(
            flatMapMergeMessageFlow(),
            loadTextMessageFlow(),
            getSocketFlow()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun flatMapMergeMessageFlow(): Flow<MainMessage> {
        return commandSharedFlow
            .flatMapMerge{command ->
                when (command) {
                    is MainCommand.LoadData -> handleLoadData(command)
                    is MainCommand.StartSocket -> handleStartSocket(command)
                    is MainCommand.StopSocket -> handleStopSocket(command)
                    else -> emptyFlow()
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadTextMessageFlow(): Flow<MainMessage> {
        return commandSharedFlow
            .filterIsInstance<MainCommand.LoadText>()
            .flatMapLatest(::handleLoadText)
            .flowOn(Dispatchers.IO)
    }

    private fun handleLoadData(command: MainCommand.LoadData): Flow<MainMessage> {
        return flow {
            Log.d("COTEA", "handleLoadData -> start")
            delay(2000)
            val data = command.value
            Log.d("COTEA","handleLoadData -> emit($data)")
            emit(data)
            Log.d("COTEA","handleLoadData -> end")
        }
            .asResource()
            .map(MainMessage::LoadedData)
            .flowOn(Dispatchers.IO)
    }

    private fun handleLoadText(command: MainCommand.LoadText): Flow<MainMessage> {
        return flow {
            Log.d("COTEA","handleLoadText -> start")
            delay(3000)
            val data = command.value.map { it.toString() }
            if (data.size > 5) throw RuntimeException("data.size > 5")
            Log.d("COTEA","handleLoadText -> emit($data)")
            emit(data)
            Log.d("COTEA","handleLoadText -> end")
        }
            .asResource()
            .map(MainMessage::LoadedText)
            .flowOn(Dispatchers.IO)
    }

    private fun getSocketFlow(): Flow<MainMessage> {
        return emulationSocket.getSocketFlow()
            .map(MainMessage::SocketData)
            .flowOn(Dispatchers.IO)
    }

    private fun handleStartSocket(command: MainCommand.StartSocket): Flow<MainMessage> {
        emulationSocket.start()
        return emptyFlow()
    }

    private fun handleStopSocket(command: MainCommand.StopSocket): Flow<MainMessage> {
        emulationSocket.stop()
        return emptyFlow()
    }
}