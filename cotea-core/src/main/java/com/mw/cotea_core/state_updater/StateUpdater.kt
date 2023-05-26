package com.mw.cotea_core.state_updater

interface StateUpdater<Event, State, SideEffect, Command> {

    fun update(event: Event, state: State): Update<State, SideEffect, Command>
}