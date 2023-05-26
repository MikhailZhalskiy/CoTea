package com.mw.cotea.main

import com.mw.cotea_core.transition.Transition
import com.mw.cotea_core.transition.TransitionListener


class MainAnalytic: TransitionListener<MainMessage, MainState> {

    override fun onTransition(transition: Transition<MainMessage, MainState>) {
        println("MainAnalytic oldState: ${transition.oldState}")
        println("MainAnalytic event: ${transition.event}")
        println("MainAnalytic newState: ${transition.newState}")
    }
}