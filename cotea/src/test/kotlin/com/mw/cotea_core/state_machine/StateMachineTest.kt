package com.mw.cotea_core.state_machine

import app.cash.turbine.turbineScope
import com.mw.cotea_core.data.Command
import com.mw.cotea_core.data.DefaultModels.COMMAND
import com.mw.cotea_core.data.DefaultModels.INITIAL_COMMAND
import com.mw.cotea_core.data.DefaultModels.INITIAL_STATE
import com.mw.cotea_core.data.DefaultModels.MESSAGE
import com.mw.cotea_core.data.DefaultModels.REDUCED_STATE
import com.mw.cotea_core.data.DefaultModels.SIDE_EFFECT
import com.mw.cotea_core.data.Message
import com.mw.cotea_core.data.SideEffect
import com.mw.cotea_core.data.State
import com.mw.cotea_core.state_updater.StateUpdater
import com.mw.cotea_core.state_updater.Update
import com.mw.cotea_core.transition.Transition
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.properties.Delegates
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class StateMachineTest {

    private val stateUpdater: StateUpdater<Message, State, SideEffect, Command> = mockk(relaxed = true)

    private val stateMachine: StateMachine<Message, State, SideEffect, Command>
        get() = lazyStateMachine.value

    private var lazyStateMachine by Delegates.notNull<Lazy<StateMachine<Message, State, SideEffect, Command>>>()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        lazyStateMachine = lazy {
            StateMachine<Message, State, SideEffect, Command>(
                stateUpdater = stateUpdater,
                initialState = INITIAL_STATE,
                dispatcherDefault = UnconfinedTestDispatcher()
            )
        }
    }

    @Test
    fun `when collect stateSource then StateMachine emit INITIAL_STATE`() = runTest {
        turbineScope {
            val stateSource = stateMachine.getStateSource().testIn(backgroundScope)
            assertEquals(INITIAL_STATE, stateSource.awaitItem())
        }
    }

    @Test
    fun `when emit MESSAGE to messageSource then StateMachine emit REDUCED_STATE`() = runTest {
        every { stateUpdater.update(state = INITIAL_STATE, message = MESSAGE) } returns Update.state(REDUCED_STATE)

        turbineScope {
            val stateSource = stateMachine.getStateSource().testIn(backgroundScope)
            stateSource.skipItems(1)
            stateMachine.onMessage(MESSAGE)
            assertEquals(REDUCED_STATE, stateSource.awaitItem())
        }
    }

    @Test
    fun `when StateUpdater return Command then StateMachine emit Command to commandSource`() = runTest {
        every { stateUpdater.update(state = any(), message = MESSAGE) } returns Update.commands(COMMAND)
        turbineScope {
            val commandSource = stateMachine.getCommandSource().testIn(backgroundScope)
            stateMachine.getStateSource().testIn(backgroundScope)
            stateMachine.onMessage(MESSAGE)
            assertEquals(COMMAND, commandSource.awaitItem())
        }
    }

    @Test
    fun `when StateUpdater return SideEffect then StateMachine emit SideEffect to sideEffectSource`() = runTest {
        every { stateUpdater.update(state = any(), message = MESSAGE) } returns Update.sideEffects(SIDE_EFFECT)
        turbineScope {
            val sideEffectSource = stateMachine.getSideEffectSource().testIn(backgroundScope)
            stateMachine.getStateSource().testIn(backgroundScope)
            stateMachine.onMessage(MESSAGE)
            assertEquals(SIDE_EFFECT, sideEffectSource.awaitItem())
        }
    }

    @Test
    fun `when execute emitInitialCommands then initialCommands emit to commandSource`() = runTest {
        turbineScope {
            val commandSource = stateMachine.getCommandSource().testIn(backgroundScope)
            stateMachine.emitInitialCommands(listOf(INITIAL_COMMAND))
            assertEquals(INITIAL_COMMAND, commandSource.awaitItem())
        }
    }

    @Test
    fun `when StateUpdater return updatedState equal previousState then StateMachine not emit updatedState`() = runTest {
        every { stateUpdater.update(state = INITIAL_STATE, message = any()) } returns Update.state(INITIAL_STATE)

        turbineScope {
            val stateSource = stateMachine.getStateSource().testIn(backgroundScope)
            stateSource.skipItems(1) // skip INITIAL_STATE
            stateMachine.onMessage(MESSAGE)
            assertFails { stateSource.awaitItem() }
        }
    }

    @Test
    fun `when StateUpdater return Update then StateMachine emit Transition to transitionSource`() = runTest {
        every { stateUpdater.update(state = INITIAL_STATE, message = MESSAGE) } returns Update.stateWithSideEffectsWithCommands(REDUCED_STATE, listOf(SIDE_EFFECT), listOf(COMMAND))
        turbineScope {
            val transitionSource = stateMachine.getTransitionSource().testIn(backgroundScope)
            stateMachine.getStateSource().testIn(backgroundScope)
            stateMachine.onMessage(MESSAGE)
            assertEquals(Transition(INITIAL_STATE, MESSAGE, REDUCED_STATE, listOf(SIDE_EFFECT), listOf(COMMAND)), transitionSource.awaitItem())
        }
    }

    @Test
    fun `when StateUpdater return updatedState equal previousState then StateMachine emit Transition to transitionSource`() = runTest {
        every { stateUpdater.update(state = INITIAL_STATE, message = MESSAGE) } returns Update.state(INITIAL_STATE)
        turbineScope {
            val transitionSource = stateMachine.getTransitionSource().testIn(backgroundScope)
            stateMachine.getStateSource().testIn(backgroundScope)
            stateMachine.onMessage(MESSAGE)
            assertEquals(Transition(INITIAL_STATE, MESSAGE, INITIAL_STATE, listOf<SideEffect>(), listOf<Command>()), transitionSource.awaitItem())
        }
    }
}