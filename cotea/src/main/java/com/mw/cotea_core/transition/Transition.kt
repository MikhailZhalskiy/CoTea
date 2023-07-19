package com.mw.cotea_core.transition

/**
 * Модель изменения состояния стейт-машины.
 *
 * @property message событие которое привело к изменению состояния (даже если состояние не изменилось).
 * @property state состояние на момент получения события [message]
 * @property updatedState новое состояние после обработки события [message]
 * @property sideEffects сайд-эффекты выброшенные стейт-машиной после обработки события [message]
 * @property commands команды выброшенные стейт-машиной после обработки события [message]
 */
data class Transition<Message, State, SideEffect, Command> internal constructor(
    val state: State,
    val message: Message,
    val updatedState: State,
    val sideEffects: List<SideEffect>,
    val commands: List<Command>
)