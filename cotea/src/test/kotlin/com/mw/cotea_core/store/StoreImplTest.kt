package com.mw.cotea_core.store

import com.mw.cotea_core.command_handler.CommandHandler
import com.mw.cotea_core.data.Command
import com.mw.cotea_core.data.DefaultModels.INITIAL_COMMAND
import com.mw.cotea_core.data.Message
import com.mw.cotea_core.data.SideEffect
import com.mw.cotea_core.data.State
import com.mw.cotea_core.state_machine.StateMachine
import com.mw.cotea_core.transition.TransitionListener
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.properties.Delegates

class StoreImplTest {

    private val stateMachine: StateMachine<Message, State, SideEffect, Command> = mockk(relaxed = true, relaxUnitFun = true)
    private val commandHandler: CommandHandler<Message, Command> = mockk(relaxed = true)
    private val initialCommands = listOf(INITIAL_COMMAND)
    private val transitionListener: TransitionListener<Message, State, SideEffect, Command> = mockk(relaxed = true)

    private val storeImpl: StoreImpl<Message, State, SideEffect, Command>
        get() = lazyStoreImpl.value

    private var lazyStoreImpl by Delegates.notNull<Lazy<StoreImpl<Message, State, SideEffect, Command>>>()

    @Before
    fun setUp() {
        lazyStoreImpl = lazy {
            StoreImpl<Message, State, SideEffect, Command>(
                stateMachine = stateMachine,
                commandHandler = commandHandler,
                initialCommands = initialCommands,
                transitionListener = transitionListener
            )
        }
    }

//    @Test
//    fun `when execute fun start then execute fun stateMachine_emitInitialCommands`() = runTest {
//        storeImpl.start(this, {}, {})
//        coVerify { stateMachine.emitInitialCommands(any()) }
//    }

    @Test
    fun `when execute fun start then execute fun stateMachine_getStateSource`() = runTest {
        storeImpl.start(this, {}, {})
        verify { stateMachine.getStateSource(any()) }
    }

    @Test
    fun `when execute fun start then execute fun stateMachine_getSideEffectSource`() = runTest {
        storeImpl.start(this, {}, {})
        verify { stateMachine.getSideEffectSource() }
    }

    @Test
    fun `when execute fun start then execute fun stateMachine_getCommandSource`() = runTest {
        storeImpl.start(this, {}, {})
        verify { stateMachine.getCommandSource() }
    }

    @Test
    fun `when execute fun start then execute fun stateMachine_getTransitionSource`() = runTest {
        storeImpl.start(this, {}, {})
        verify { stateMachine.getTransitionSource() }
    }
}