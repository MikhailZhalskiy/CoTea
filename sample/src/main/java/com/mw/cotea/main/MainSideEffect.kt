package com.mw.cotea.main

sealed interface MainSideEffect {
    data class LoadedData(val value: Int): MainSideEffect
}