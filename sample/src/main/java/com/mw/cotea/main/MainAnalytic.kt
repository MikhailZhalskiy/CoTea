package com.mw.cotea.main

import com.mw.cotea_core.transition.Transition
import com.mw.cotea_core.transition.TransitionListener


class MainAnalytic: TransitionListener<MainMessage, MainState> {

    override fun onTransition(transition: Transition<MainMessage, MainState>) {
        println("MainAnalytic state: ${transition.state}")
        println("MainAnalytic message: ${transition.message}")
        println("MainAnalytic newState: ${transition.newState}")
    }
}