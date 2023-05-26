package com.mw.cotea_core.state_updater

interface StateUpdater<Message, State, SideEffect, Command> {

    fun update(message: Message, state: State): Update<State, SideEffect, Command>
}