package com.mw.cotea

import com.mw.cotea.main.MainState
import com.mw.cotea.main.MainViewState

class MainConvertor {

    fun toMainViewState(mainState: MainState): MainViewState {
        return MainViewState(
            words = toWords(mainState),
            isLoadingList = toIsLoadingList(mainState),
            isLoadingOnButton = toIsLoadingOnButton(mainState),
            size = toSize(mainState)
        )
    }

    private fun toSize(mainState: MainState): String {
        return if(mainState.loadData is Resource.Data) mainState.loadData.value.toString() else ""
    }

    private fun toIsLoadingList(mainState: MainState): Boolean {
        return mainState.words == Resource.Loading
    }

    private fun toWords(mainState: MainState): List<String> {
        return when(mainState.words) {
            is Resource.Loading -> emptyList()
            is Resource.Data -> mainState.words.value
            is Resource.Error -> listOf("Error loading data")
        }
    }

    private fun toIsLoadingOnButton(mainState: MainState): Boolean {
        return mainState.loadData == Resource.Loading
    }
}