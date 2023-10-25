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
import com.mw.cotea.main.MainStateUpdaterDsl
import com.mw.cotea.main.MainViewState
import com.mw.cotea.util.ExactlyOnceEventBus
import com.mw.cotea.util.SingleShotEvent
import com.mw.cotea.util.throttleFirst
import com.mw.cotea_core.store.Store
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val mainFactory: MainFactory = MainFactory(
        stateUpdater = MainStateUpdaterDsl(),
        commandHandler = MainCommandHandler(),
        transitionListener = MainAnalytic()
    ),
    private val mainConvertor: MainConvertor = MainConvertor()
): ViewModel() {

    private val _mainViewState = MutableStateFlow(MainViewState())
    val mainViewState = _mainViewState.asStateFlow()

    // пример отправки сайд эффектов
    private val _mainSideEffect = SingleShotEvent<MainSideEffect>()
    val mainSideEffect = _mainSideEffect.events

    // пример отправки сайд эффектов предложенный Романом Елизаровым
    val exactlyOnceEventBus = ExactlyOnceEventBus<MainSideEffect>()

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

    @OptIn(FlowPreview::class)
    private fun collectUiEvent() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.d("COTEA", "coroutineContext = $coroutineContext : throwable = $throwable")
        }

        store.start(
            coroutineScope = viewModelScope,
            coroutineExceptionHandler = coroutineExceptionHandler,
            actionState = { mainState ->
                _mainViewState.emit(
                    mainConvertor.toMainViewState(mainState)
                )
            },
            actionSideEffect = { sideEffect ->
                _mainSideEffect.postEvent(sideEffect)
//                exactlyOnceEventBus.send(sideEffect)
            }
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
}