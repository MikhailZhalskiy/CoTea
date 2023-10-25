package com.mw.cotea.main

import com.mw.cotea.util.Resource
import com.mw.cotea_core.state_updater.dsl.StateUpdaterDsl

class MainStateUpdaterDsl: StateUpdaterDsl<MainMessage, MainState, MainSideEffect, MainCommand>() {

    override fun updateDsl(state: MainState, message: MainMessage) {
        when(message) {
            is MainMessage.OnInputText -> updateOnInputText(state, message)
            is MainMessage.LoadedText -> updateLoadedText(state, message)
            is MainMessage.OnLoadDataClick -> updateOnLoadDataClick(state, message)
            is MainMessage.LoadedData -> updateLoadedData(state, message)
            is MainMessage.SocketData -> updateSocketData(state, message)
        }
    }

    private fun updateOnInputText(
        state: MainState,
        message: MainMessage.OnInputText
    ) {
        updateSate { copy(inputText = message.value) }
        command { MainCommand.LoadText(message.value) }
    }

    private fun updateLoadedText(
        state: MainState,
        message: MainMessage.LoadedText
    ) {
        updateSate { copy(words = message.value) }
    }

    private fun updateOnLoadDataClick(
        state: MainState,
        message: MainMessage.OnLoadDataClick
    ) {
        val value: Int = if (state.words is Resource.Data) state.words.value.size else 0
        command { MainCommand.LoadData(value) }
    }

    private fun updateLoadedData(
        state: MainState,
        message: MainMessage.LoadedData
    ) {
        if (message.value is Resource.Data) {
            sideEffect { MainSideEffect.LoadedData(message.value.value) }
        }

        updateSate { copy(loadData = message.value) }
    }

    private fun updateSocketData(
        state: MainState,
        message: MainMessage.SocketData
    ) {
        sideEffect { MainSideEffect.SocketData(message.value) }
    }
}