package com.mw.cotea.main

import com.mw.ext.Resource


sealed interface MainMessage {
    data class OnInputText(val value: String) : MainMessage
    data class LoadedText(val value: Resource<List<String>>) : MainMessage
    data object OnLoadDataClick : MainMessage
    data class LoadedData(val value: Resource<Int>) : MainMessage
    data class SocketData(val value: Int) : MainMessage
}