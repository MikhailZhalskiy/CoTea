package com.mw.cotea_core.transition

interface TransitionListener<Message, State> {
    fun onTransition(transition: Transition<Message, State>)
}