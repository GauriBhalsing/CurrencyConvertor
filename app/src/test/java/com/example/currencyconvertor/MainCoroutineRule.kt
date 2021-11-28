package com.example.currencyconvertor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.runner.Description
import org.junit.rules.TestWatcher
import kotlin.coroutines.ContinuationInterceptor

/**
 * Sets the main coroutine dispatcher to a [TestCoroutineScope] for unit testing.A
 * [TestCoroutineScope] provides controls over the execution of coroutine.
 * */
@ExperimentalCoroutinesApi
class MainCoroutineRule: TestWatcher(), TestCoroutineScope by TestCoroutineScope() {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher)
    }
    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
        testCoroutineScope.runBlockingTest { block() }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}