package com.saket.kotlinflows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Here i explore Lists, sequence, coroutines and simple flows. */

/*
It is possible to iterate over a list and print values.
But its different in Flows because, flows allows you to
generate and collect the emissions asynchronously.
Then there are intermediate operators to transform/map the emissions.
 */
private fun printFromList() {
    val mySimpleList = arrayOf(1, 2, 3)
    mySimpleList.forEach { println(it) }
}

/*
A collection that perform some computation to
generate each value, can be a Sequence.
Sequence type represents lazily evaluated collections.
It also provides intermediate methods like flow.
Sequence can also be converted to Flow.
 */
private fun printFromSequence() {
    val mySimpleSequence = sequence {
        for (i in 1..3) {
            Thread.sleep(1000) // Some computation
            yield(i)
        }
    }
    mySimpleSequence.map { value -> value * 10 }.iterator().forEach {
        println("Sequence emits $it")
    }
}

/*
Here we use suspend function in coroutine to prepare the list of
elements to emit before sending them downstream.

So, element generation and collection is happening in suspend functions,
which is similar to Flows. But here, first the entire list is created in
a loop and then we iterate over its elements. This is not a stream of elements.
Essentially the suspend function returns a single list instead of a stream.

Also, it can be seen here that collection of elements happens in a different scope
compared to element creation. This violates first principle of flows that is context
preservation.
 */
private fun simpleSuspendedList() {
    val sampleList = mutableListOf<String>()
    CoroutineScope(Dispatchers.IO).launch {
        println("Creating list on ${Thread.currentThread().name}")
        // list creation in coroutine
        for (i in 1..5) {
            delay(1000) // Some computation that suspends
            sampleList.add("Suspend functions says $i")
        }

        // collecting list on Main Thread...
        withContext(Dispatchers.Main) {
            sampleList.asReversed().forEach { value ->
                println("$value on ${Thread.currentThread().name}")
            }
        }
    }
}

/*
Above, we can only return all the values at once.
To represent the stream of values that are being
asynchronously computed, we can use a Flow<Int> type.
Flow emission and collection happens in the same context.
 */
private fun simpleFlow() {
    val flowOfInts = flow { // Flow builder
        // Code inside the flow { ... } builder block can suspend.
        for (i in 1..10) {
            delay(1000) // Some async computation
            println("Emitting $i from flow on ${Thread.currentThread().name}")
            emit(i) // emit next value
        }
    }

    CoroutineScope(Dispatchers.Main).launch {
        flowOfInts.filter { value -> value % 2 == 0 }.map { value: Int -> value + 100 }.collect {
            println("Collecting $it from flow on ${Thread.currentThread().name}")
        }
    }
}
