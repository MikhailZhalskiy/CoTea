package com.mw.cotea_core.data

data class State(val state: String)
data class Message(val message: String)
data class SideEffect(val sideEffect: String)
data class Command(val command: String)

object DefaultModels{
    val INITIAL_STATE = State("initial_state")
    val REDUCED_STATE = State("reduced_state")

    val MESSAGE = Message("any_message")

    val SIDE_EFFECT = SideEffect("any_side_effect")

    val INITIAL_COMMAND = Command("initial_command")
    val COMMAND = Command("any_command")
}