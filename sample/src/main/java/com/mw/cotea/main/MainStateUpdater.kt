package com.mw.cotea.main

import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.state_updater.Update

class MainStateUpdater: StateUpdater<MainMessage, MainState, MainSideEffect, MainCommand> {

    override fun update(
        state: MainState,
        message: MainMessage
    ): Update<MainState, MainSideEffect, MainCommand> {
        return when(message) {
            is MainMessage.OneClick -> reduceOneClick(state, message)
            is MainMessage.TwoClick -> reduceTwoClick(state, message)
            is MainMessage.LoadedOneClick -> reduceLoadedOneClick(state, message)
            is MainMessage.LoadedTwoClick -> reduceLoadedTwoClick(state, message)
        }
    }

    private fun reduceOneClick(
        state: MainState,
        message: MainMessage.OneClick
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.stateWithSideEffectsWithCommands(
            state = state.copy(countOneClick = state.countOneClick + 1),
            commands = listOf(MainCommand.LoadOne)
        )
    }

    private fun reduceTwoClick(
        state: MainState,
        message: MainMessage.TwoClick
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.stateWithSideEffectsWithCommands(
            state = state.copy(countTwoClick = state.countTwoClick + 1),
            commands = listOf(MainCommand.LoadTwo)
        )
    }

    private fun reduceLoadedOneClick(
        state: MainState,
        message: MainMessage.LoadedOneClick
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.state(state.copy(one = message.one))
    }

    private fun reduceLoadedTwoClick(
        state: MainState,
        message: MainMessage.LoadedTwoClick
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.state(state.copy(two = message.two))
    }


}