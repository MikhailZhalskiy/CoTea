package com.mw.cotea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mw.cotea.main.MainAnalytic
import com.mw.cotea.main.MainCommand
import com.mw.cotea.main.MainCommandHandler
import com.mw.cotea.main.MainFactory
import com.mw.cotea.main.MainMessage
import com.mw.cotea.main.MainSideEffect
import com.mw.cotea.main.MainState
import com.mw.cotea.main.MainStateUpdater
import com.mw.cotea.main.MainViewState
import com.mw.cotea_core.store.Store
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val mainFactory: MainFactory = MainFactory(
        stateUpdater = MainStateUpdater(),
        commandHandler = MainCommandHandler(),
        transitionListener = MainAnalytic()
    ),
    private val mainConvertor: MainConvertor = MainConvertor()
): ViewModel() {

    private val _mainViewState = MutableStateFlow(MainViewState())
    val mainViewState = _mainViewState.asStateFlow()

    private val _mainSideEffect = MutableSharedFlow<MainSideEffect>()
    val mainSideEffect = _mainSideEffect.asSharedFlow()

    private val uiMessage = MutableSharedFlow<MainMessage>()
    private val uiMessageOnInputText = MutableSharedFlow<MainMessage.OnInputText>()

    private val store: Store<MainMessage, MainState, MainSideEffect, MainCommand> = mainFactory.createStore(::MainState)

    init {
        collectUiEvent()
    }

    fun onInputText(value: String) {
        viewModelScope.launch(Dispatchers.Default) { uiMessageOnInputText.emit(MainMessage.OnInputText(value)) }
    }

    fun onLoadDataClick() {
        viewModelScope.launch(Dispatchers.Default) { uiMessage.emit(MainMessage.OnLoadDataClick) }
    }

    private fun collectUiEvent() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.d(TAG, "coroutineContext = $coroutineContext : throwable = $throwable")
        }

        store.start(
            coroutineScope = viewModelScope,
            coroutineExceptionHandler = coroutineExceptionHandler,
            actionState = { mainState ->
                _mainViewState.emit(
                    mainConvertor.toMainViewState(mainState)
                )
            },
            actionSideEffect = _mainSideEffect::emit
        )

        uiMessageOnInputText
            .debounce(300L)
            .onEach(store::onMessage)
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)

        uiMessage
            .throttleFirst(300L)
            .onEach(store::onMessage)
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "COTEA"
    }
}

