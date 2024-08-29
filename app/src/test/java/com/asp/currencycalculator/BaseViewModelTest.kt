package com.asp.currencycalculator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseViewModelTest {
    @Rule @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    protected val lifeCycleOwner = mockk<LifecycleOwner>()

    private val lifeCycleRegistry = LifecycleRegistry(lifeCycleOwner).also {
        every { lifeCycleOwner.lifecycle } returns it
    }

    @Before
    open fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher.dispatcher)
        lifeCycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifeCycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    @After
    open fun teardown() {
        if (lifeCycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifeCycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            lifeCycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
        Dispatchers.resetMain()
        TestCoroutineDispatcher.dispatcher.cancel()
        TestCoroutineDispatcher.dispatcher.cancelChildren()
    }
}

@ExperimentalCoroutinesApi
object TestCoroutineDispatcher {
    private val coroutineScheduler = TestCoroutineScheduler()

    val dispatcher = StandardTestDispatcher(coroutineScheduler)
}