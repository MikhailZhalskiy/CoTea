package com.mw.cotea.main

import com.mw.cotea.Resource


sealed interface MainMessage {

    object OneClick : MainMessage
    object TwoClick : MainMessage

    data class LoadedOneClick(val one: Resource<Int>) : MainMessage
    data class LoadedTwoClick(val two: Resource<Int>) : MainMessage
}