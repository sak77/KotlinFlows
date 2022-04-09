package com.saket.kotlinflows

import kotlin.system.measureTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/**
 * Flows work sequentially. In the sense that if it takes 100ms to emit a flow and another 300ms to
 * collect it. Then overall it will take 400ms to process an item in the flow. If there are 3 items
 * in a flow then totally it will take approx 1200ms to process.
 *
 * But the buffer() operator tells the flow to emit the current flow item concurrently with the
 * collecting code, as opposed to them running sequentially. This reduces the overall time required
 * to process the flow.
 */

/*
Here each flow emit takes 100ms + collection takes 300ms.
So, overall time for each item is 400ms. Times 5 = 2000ms approx
for regular flows.

But using buffering, the time is reduced to approx 1650ms.
*/
fun testFlowBuffering() {
    val time = measureTimeMillis {
        runBlocking {
            mySimpleFlow()
                .buffer() // buffer emissions, dont wait
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
