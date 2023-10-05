package com.mw.cotea.main

import com.mw.cotea.asResource
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

class MainCommandHandler: CommandHandler<MainMessage, MainCommand> {

    private val commandSharedFlow = MutableSharedFlow<MainCommand>(Int.MAX_VALUE)

    override suspend fun onCommand(command: MainCommand) {
        commandSharedFlow.emit(command)
    }

    override fun getMessageSource(): Flow<MainMessage> {
        return merge(
            flatMapMergeMessageFlow(),
            loadTextMessageFlow()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun flatMapMergeMessageFlow(): Flow<MainMessage> {
        return commandSharedFlow
            .flatMapMerge{command ->
                when (command) {
                    is MainCommand.LoadData -> handleLoadData(command)
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
            println("handleLoadData -> start")
            delay(2000)
            val data = command.value
            println("handleLoadData -> emit($data)")
            emit(data)
            println("handleLoadData -> end")
        }
            .asResource()
            .map(MainMessage::LoadedData)
            .flowOn(Dispatchers.IO)
    }

    private fun handleLoadText(command: MainCommand.LoadText): Flow<MainMessage> {
        return flow {
            println("handleLoadText -> start")
            delay(3000)
            val data = command.value.map { it.toString() }
            if (data.size > 5) throw RuntimeException("data.size > 5")
            println("handleLoadText -> emit($data)")
            emit(data)
            println("handleLoadText -> end")
        }
            .asResource()
            .map(MainMessage::LoadedText)
            .flowOn(Dispatchers.IO)
    }
}