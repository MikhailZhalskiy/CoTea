package com.mw.cotea_core.state_updater

/**
 * Получает текущего состояния [State] и [Message] преобразовывая их в [Update]
 * @see Update
 */
interface StateUpdater<Message, State, SideEffect, Command> {

    fun update(state: State, message: Message): Update<State, SideEffect, Command>
}