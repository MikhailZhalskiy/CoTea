package com.mw.cotea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mw.cotea.main.MainAnalytic
import com.mw.cotea.main.MainCommand
import com.mw.cotea.main.MainCommandHandler
import com.mw.cotea.main.MainCommandHandlerSwitchMap
import com.mw.cotea.main.MainFactory
import com.mw.cotea.main.MainMessage
import com.mw.cotea.main.MainSideEffect
import com.mw.cotea.main.MainState
import com.mw.cotea.main.MainStateUpdater
import com.mw.cotea_core.store.Store
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivityViewModel(
    mainFactory: MainFactory = MainFactory(
        stateUpdater = MainStateUpdater(),
        commandHandler = MainCommandHandlerSwitchMap(),//MainCommandHandler(),
        transitionListener = MainAnalytic()
    )
): ViewModel() {

    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    private val _mainSideEffect = MutableSharedFlow<MainSideEffect>()
    val mainSideEffect = _mainSideEffect.asSharedFlow()

    private val uiMessage = MutableSharedFlow<MainMessage>()

    private val store: Store<MainMessage, MainState, MainSideEffect, MainCommand> = mainFactory.createStore(::MainState)

    init {
        collectUiEvent()
    }

    fun loadOne() {
        viewModelScope.launch { uiMessage.emit(MainMessage.OneClick) }
    }

    fun loadTwo() {
        viewModelScope.launch { uiMessage.emit(MainMessage.TwoClick) }
    }

    private fun collectUiEvent() {
        uiMessage.onEach(store::onMessage)
            .launchIn(viewModelScope)

        store.start(
            viewModelScope,
            _mainState::emit,
            _mainSideEffect::emit
        )
    }
}