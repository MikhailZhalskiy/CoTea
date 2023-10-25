package com.mw.cotea_core.store

import com.mw.cotea_core.command_handler.CommandHandler
import com.mw.cotea_core.data.Command
import com.mw.cotea_core.data.DefaultModels.INITIAL_COMMAND
import com.mw.cotea_core.data.DefaultModels.INITIAL_STATE
import com.mw.cotea_core.data.Message
import com.mw.cotea_core.data.SideEffect
import com.mw.cotea_core.data.State
import com.mw.cotea_core.state_machine.StateMachine
import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.transition.TransitionListener
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.properties.Delegates

@OptIn(ExperimentalCoroutinesApi::class)
internal class StoreImplTest {

    private val stateMachine: StateMachine<Message, State, SideEffect, Command> = mockk(relaxed = true)
    private val commandHandler: CommandHandler<Message, Command> = mockk(relaxed = true)
    private val initialCommands = listOf(INITIAL_COMMAND)
    private val transitionListener: TransitionListener<Message, State, SideEffect, Command> = mockk(relaxed = true)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->  }

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

    @Test
    fun `when execute fun start then execute fun stateMachine_getStateSource`() = runTest {
        storeImpl.start(
            backgroundScope,
            coroutineDispatcher = UnconfinedTestDispatcher(),
            coroutineExceptionHandler = coroutineExceptionHandler,
            actionState = {}) {}
        verify(exactly = 1) { stateMachine.getStateSource(any()) }
    }

    @Test
    fun `when execute fun start then execute fun stateMachine_getSideEffectSource`() = runTest {
        storeImpl.start(
            backgroundScope,
            coroutineDispatcher = UnconfinedTestDispatcher(),
            coroutineExceptionHandler = coroutineExceptionHandler,
            actionState = {}) {}
        verify(exactly = 1) { stateMachine.getSideEffectSource(any()) }
    }

    @Test
    fun `when execute fun start then execute fun stateMachine_getCommandSource`() = runTest {
        storeImpl.start(
            backgroundScope,
            coroutineDispatcher = UnconfinedTestDispatcher(),
            coroutineExceptionHandler = coroutineExceptionHandler,
            actionState = {}) {}
        verify(exactly = 1) { stateMachine.getCommandSource(any()) }
    }

    @Test
    fun `when execute fun start then execute fun stateMachine_getTransitionSource`() = runTest {
        storeImpl.start(
            backgroundScope,
            coroutineDispatcher = UnconfinedTestDispatcher(),
            coroutineExceptionHandler = coroutineExceptionHandler,
            actionState = {}) {}
        verify(exactly = 1) { stateMachine.getTransitionSource(any()) }
    }

    @Test
    fun `when execute fun start then execute fun stateMachine_emitInitialCommands`() = runTest {
        val stateUpdater: StateUpdater<Message, State, SideEffect, Command> = mockk(relaxed = true)
        val stateMachine = spyk(StateMachine(
            stateUpdater = stateUpdater,
            INITIAL_STATE,
            UnconfinedTestDispatcher()
        ))
        lazyStoreImpl = lazy {
            StoreImpl<Message, State, SideEffect, Command>(
                stateMachine = stateMachine,
                commandHandler = commandHandler,
                initialCommands = initialCommands,
                transitionListener = transitionListener
            )
        }

        storeImpl.start(
            backgroundScope,
            coroutineDispatcher = UnconfinedTestDispatcher(),
            coroutineExceptionHandler = coroutineExceptionHandler,
            actionState = {}) {}

        coVerify { stateMachine.emitInitialCommands(any()) }
    }
}