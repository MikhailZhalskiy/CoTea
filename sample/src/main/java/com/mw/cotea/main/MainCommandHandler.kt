package com.mw.cotea.main

import com.mw.cotea.asResource
import com.mw.cotea_core.command_handler.CommandHandler
import com.mw.cotea_core.command_handler.CommandHandlerDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.random.Random

class MainCommandHandler: CommandHandlerDefault<MainMessage, MainCommand>() {

    override suspend fun execute(command: MainCommand): Flow<MainMessage> {
        return when (command) {
            MainCommand.LoadOne -> handleLoadOne(command)
            MainCommand.LoadTwo -> handleLoadTwo(command)
        }
    }

    private fun handleLoadOne(command: MainCommand): Flow<MainMessage> {
        return flow {
            println("handleLoadOne -> start")
            Thread.sleep(5000)
            val data = Random.nextInt(100)
            println("handleLoadOne -> pre emit($data)")
            emit(data)
            println("handleLoadOne -> end")
        }
            .flowOn(Dispatchers.IO)
            .asResource()
            .map(MainMessage::LoadedOneClick)
    }

    private fun handleLoadTwo(command: MainCommand): Flow<MainMessage> {
        return flow {
            println("handleLoadTwo -> start")
            Thread.sleep(5000)
            val data = Random.nextInt(100)
            println("handleLoadTwo -> pre emit($data)")
            emit(data)
            println("handleLoadTwo -> end")
        }
            .flowOn(Dispatchers.IO)
            .asResource()
            .map(MainMessage::LoadedTwoClick)
    }
}

class MainCommandHandlerSwitchMap: CommandHandler<MainMessage, MainCommand> {

    private val commandSharedFlow = MutableSharedFlow<MainCommand>(Int.MAX_VALUE)

    override fun getMessageSource(): Flow<MainMessage> {
        return merge(
            flatMapMergeMessageFlow(),
            flatMapLatestMessageFlow()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun flatMapMergeMessageFlow(): Flow<MainMessage> {
        return commandSharedFlow
//            .filter { command ->
//                command is MainCommand.LoadOne
//            }
            .flatMapMerge{command ->
                when (command) {
                    MainCommand.LoadOne -> handleLoadOne(command)
                    else -> emptyFlow()
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun flatMapLatestMessageFlow(): Flow<MainMessage> {
        return commandSharedFlow
//            .filter { command ->
//                command is MainCommand.LoadTwo
//            }
            .flatMapLatest{command ->
                when (command) {
                    MainCommand.LoadTwo -> handleLoadTwo(command)
                    else -> emptyFlow()
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun onCommand(command: MainCommand) {
        commandSharedFlow.emit(command)
    }

    private fun handleLoadOne(command: MainCommand): Flow<MainMessage> {
        return flow {
            println("handleLoadOne -> start")
            delay(5000)
            val data = Random.nextInt(100)
            println("handleLoadOne -> emit($data)")
            emit(data)
            println("handleLoadOne -> end")
        }
            .flowOn(Dispatchers.IO)
            .asResource()
            .map(MainMessage::LoadedOneClick)
    }

    private fun handleLoadTwo(command: MainCommand): Flow<MainMessage> {
        return flow {
            println("handleLoadTwo -> start")
            delay(5000)
            val data = Random.nextInt(100)
            println("handleLoadTwo -> emit($data)")
            emit(data)
            println("handleLoadTwo -> end")
        }
            .flowOn(Dispatchers.IO)
            .asResource()
            .map(MainMessage::LoadedTwoClick)
    }
}