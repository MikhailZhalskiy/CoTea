package com.mw.cotea.main

import com.mw.cotea.Resource
import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.state_updater.Update

class MainStateUpdater: StateUpdater<MainMessage, MainState, MainSideEffect, MainCommand> {

    override fun update(
        state: MainState,
        message: MainMessage
    ): Update<MainState, MainSideEffect, MainCommand> {
        return when(message) {
            is MainMessage.OnInputText -> updateOnInputText(state, message)
            is MainMessage.LoadedText -> updateLoadedText(state, message)
            is MainMessage.OnLoadDataClick -> updateOnLoadDataClick(state, message)
            is MainMessage.LoadedData -> updateLoadedData(state, message)
        }
    }

    private fun updateOnInputText(
        state: MainState,
        message: MainMessage.OnInputText
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.stateWithSideEffectsWithCommands(
            state.copy(inputText = message.value),
            commands = listOf(MainCommand.LoadText(message.value))
        )
    }

    private fun updateLoadedText(
        state: MainState,
        message: MainMessage.LoadedText
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.state(state.copy(words = message.value))
    }

    private fun updateOnLoadDataClick(
        state: MainState,
        message: MainMessage.OnLoadDataClick
    ): Update<MainState, MainSideEffect, MainCommand> {
        val value: Int = if (state.words is Resource.Data) state.words.value.size else 0
        return Update.commands(MainCommand.LoadData(value))
    }

    private fun updateLoadedData(
        state: MainState,
        message: MainMessage.LoadedData
    ): Update<MainState, MainSideEffect, MainCommand> {
        val sideEffect = if (message.value is Resource.Data) MainSideEffect.LoadedData(message.value.value) else null
        return Update.stateWithSideEffectsWithCommands(
            state.copy(loadData = message.value),
            sideEffects = sideEffect?.let { listOf(it) }
        )
    }
}