package com.mw.cotea.main

sealed interface MainSideEffect {
    data class LoadedData(val value: Int): MainSideEffect
    data class SocketData(val value: Int) : MainSideEffect
}