package com.mw.cotea.main

import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.state_updater.Update

class MainStateUpdater: StateUpdater<MainMessage, MainState, MainSideEffect, MainCommand> {

    override fun update(
        event: MainMessage,
        state: MainState
    ): Update<MainState, MainSideEffect, MainCommand> {
        return when(event) {
            is MainMessage.OneClick -> reduceOneClick(event, state)
            is MainMessage.TwoClick -> reduceTwoClick(event, state)
            is MainMessage.LoadedOneClick -> reduceLoadedOneClick(event, state)
            is MainMessage.LoadedTwoClick -> reduceLoadedTwoClick(event, state)
        }
    }

    private fun reduceOneClick(
        event: MainMessage.OneClick,
        state: MainState
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.stateWithSideEffectsWithCommands(
            state = state.copy(countOneClick = state.countOneClick + 1),
            commands = listOf(MainCommand.LoadOne)
        )
    }

    private fun reduceTwoClick(
        event: MainMessage.TwoClick,
        state: MainState
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.stateWithSideEffectsWithCommands(
            state = state.copy(countTwoClick = state.countTwoClick + 1),
            commands = listOf(MainCommand.LoadTwo)
        )
    }

    private fun reduceLoadedOneClick(
        event: MainMessage.LoadedOneClick,
        state: MainState
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.state(state.copy(one = event.one))
    }

    private fun reduceLoadedTwoClick(
        event: MainMessage.LoadedTwoClick,
        state: MainState
    ): Update<MainState, MainSideEffect, MainCommand> {
        return Update.state(state.copy(two = event.two))
    }


}