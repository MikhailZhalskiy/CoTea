package com.mw.cotea.main

sealed interface MainCommand {
    data class LoadText(val value: String) : MainCommand
    data class LoadData(val value: Int) : MainCommand
}