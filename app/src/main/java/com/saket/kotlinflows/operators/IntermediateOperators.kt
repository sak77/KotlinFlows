package com.saket.kotlinflows.operators

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking

/**
 * There are many intermediate operators in flows. These can be applied to upstream flow and return
 * a downstream flow. They are not suspending functions. Common ones are map() and filter(). To see
 * all intermediate operators:
 * https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/index.html
 */
fun applyMap() {
    val mySimpleList = arrayOf(1, 2, 3)
    val myLimitedFLow = flowOf(1, 3, 4)

    runBlocking {
        // transform value using map()
        mySimpleList.asFlow().map { it * 100 }.collect { println("Transformed value $it") }

        // Filter even numbers
        myLimitedFLow.filter { it % 2 == 0 }.collect { println("Only even numbers: $it") }
    }
}

/*Transform operator is most general intermediate
operator. It can be used to imitate simple transformations
like map and filter, as well as implement more
complex transformations. Using the transform operator,
we can emit arbitrary values an arbitrary number of
times.
 */
fun transformOperator() {
    runBlocking {
        mySimpleSequence
            .asFlow()
            .transform {
                emit("Making request $this")
                emit(it + 100)
            }
            .collect { println(it) }
    }
}

val mySimpleSequence = sequence {
    for (i in 1..3) {
        Thread.sleep(100) // Some computation
        yield(i)
    }
}
