package com.saket.kotlinflows

import kotlin.system.measureTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/**
 * It may not be necessary to process each value, but instead, only most recent ones. In this case,
 * the conflate operator can be used to skip intermediate values when a collector is too slow to
 * process them.
 */
fun testFlowConflation() {
    val time = measureTimeMillis {
        runBlocking {
            mySimpleFlow()
                .conflate() // conflate emissions, dont process each one
                .collect {
                    delay(300)
                    println("Collect $it")
                }
        }
    }
    println("Collected in $time ms")
}

private fun mySimpleFlow() = flow {
    for (i in 1..5) {
        delay(100) // Pretend to do some work
        emit(i)
    }
}
