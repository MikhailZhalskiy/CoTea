package com.mw.cotea.main

import com.mw.cotea_core.store.Store
import com.mw.cotea_core.store.StoreFactory

class MainFactory(
    private val stateUpdater: MainStateUpdater,
    private val commandHandler: MainCommandHandlerSwitchMap,
    private val transitionListener: MainAnalytic
) {

    fun createStore(
        initialState: () -> MainState
    ): Store<MainMessage, MainState, MainSideEffect, MainCommand> {
        return StoreFactory.createStore(
            stateUpdater = stateUpdater,
            commandHandler = commandHandler,
            initialState = initialState(),
            initialCommands = emptyList(),
            transitionListener = null//transitionListener
        )
    }
}