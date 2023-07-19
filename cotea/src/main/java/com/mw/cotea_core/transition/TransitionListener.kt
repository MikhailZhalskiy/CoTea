package com.mw.cotea_core.transition

/**
 * При необходимости слушать изменения состояния при реакции на события [Message]
 * (например для отправки метрик или логирования, дебага),
 * можно предоставить [TransitionListener] в который будет брошена полная информация об [Transition],
 * содержащая данные о том какое событие произошло, какое состояние было, и новое состояние с набором сайд-эффектов и команд.
 * Если при получении события состояние не изменилось и не было "выброшено" сайд-эффектов или команд
 * [TransitionListener] все равно будет уведомлен о получении события.
 */
interface TransitionListener<Message, State, SideEffect, Command> {
    fun onTransition(transition: Transition<Message, State, SideEffect, Command>)
}