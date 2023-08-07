package com.mw.cotea_core.store

import com.mw.cotea_core.command_handler.CommandHandler
import com.mw.cotea_core.data.Command
import com.mw.cotea_core.data.DefaultModels.INITIAL_COMMAND
import com.mw.cotea_core.data.Message
import com.mw.cotea_core.data.SideEffect
import com.mw.cotea_core.data.State
import com.mw.cotea_core.state_machine.StateMachine
import com.mw.cotea_core.transition.TransitionListener
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

internal class StoreImplTest {

    private val stateMachine: StateMachine<Message, State, SideEffect, Command> = mockk(relaxed = true)
    private val commandHandler: CommandHandler<Message, Command> = mockk(relaxed = true)
    private val initialCommands = listOf(INITIAL_COMMAND)
    private val transitionListener: TransitionListener<Message, State, SideEffect, Command> = mockk(relaxed = true)

    private lateinit var storeImpl: StoreImpl<Message, State, SideEffect, Command>

    @Before
    fun setUp() {
        storeImpl = StoreImpl(
            stateMachine = stateMachine,
            commandHandler = commandHandler,
            initialCommands = initialCommands,
            transitionListener = transitionListener
        )
    }

    @Test
    fun `when call start then fun stateMachine_emitInitialCommands is called after all source initialization`() =
        runBlocking(Dispatchers.IO) {
            storeImpl.start(this, {}, {})
            coVerifyOrder {
                stateMachine.getTransitionSource()
                stateMachine.emitInitialCommands(any())
            }
            coVerifyOrder {
                stateMachine.getSideEffectSource()
                stateMachine.emitInitialCommands(any())
            }
            coVerifyOrder {
                stateMachine.getCommandSource()
                stateMachine.emitInitialCommands(any())
            }
            coVerify {
                stateMachine.getStateSource()
                stateMachine.emitInitialCommands(any())
            }
        }
}