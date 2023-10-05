package com.mw.cotea.main

data class MainViewState(
    val words:List<String> = listOf(),
    val isLoadingList: Boolean = false,
    val isLoadingOnButton: Boolean = false,
    val size: String = ""
)