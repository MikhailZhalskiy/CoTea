package com.mw.cotea.main

import android.util.Log
import com.mw.cotea_core.transition.Transition
import com.mw.cotea_core.transition.TransitionListener


class MainAnalytic: TransitionListener<MainMessage, MainState, MainSideEffect, MainCommand> {

    override fun onTransition(transition: Transition<MainMessage, MainState, MainSideEffect, MainCommand>) {
        Log.d("COTEA", "MainAnalytic state: ${transition.state}")
        Log.d("COTEA", "MainAnalytic message: ${transition.message}")
        Log.d("COTEA", "MainAnalytic updatedState: ${transition.updatedState}")
        Log.d("COTEA", "MainAnalytic commands: ${transition.commands}")
        Log.d("COTEA", "MainAnalytic sideEffects: ${transition.sideEffects}")
    }
}