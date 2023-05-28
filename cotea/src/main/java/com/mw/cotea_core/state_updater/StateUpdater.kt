package com.mw.cotea_core.state_updater

interface StateUpdater<Message, State, SideEffect, Command> {

    fun update(state: State, message: Message): Update<State, SideEffect, Command>
}