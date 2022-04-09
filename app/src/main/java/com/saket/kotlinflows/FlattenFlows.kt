package com.saket.kotlinflows

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/**
 * Flows represent asynchronously received sequences of values, so it is quite easy to get in a
 * situation where each value triggers a request for another sequence of values.
 *
 * For example, we can have the following function that returns a flow of two strings 500 ms apart:
 *
 * fun requestFlow(i: Int): Flow<String> = flow { emit("$i: First") delay(500) // wait 500 ms
 * emit("$i: Second") } Now if we have a flow of three integers and call requestFlow for each of
 * them like this: (1..3).asFlow().map { requestFlow(it) }
 *
 * Then we end up with a flow of flows (Flow<Flow<String>>) that needs to be flattened into a single
 * flow for further processing.
 *
 * Collections and sequences have flatten and flatMap operators for this. However, due to the
 * asynchronous nature of flows they call for different modes of flattening, as such, there is a
 * family of flattening operators on flows.
 */
val colorsFlow = listOf("Red", "Yellow", "Green", "Blue").asFlow()

fun printColor(color: String) = flow {
    emit("$color 1")
    kotlinx.coroutines.delay(500)
    emit("$color 2")
}

/*
FlatmapConcat -
 They wait for the inner flow to complete before
 starting to collect the next one.
 */
fun flatmapConcat() {
    val startTime = System.currentTimeMillis()
    runBlocking {
        colorsFlow.flatMapConcat { color -> printColor(color) }.collect {
            println("$it ${System.currentTimeMillis() - startTime} ms from start")
        }
    }
}

/*
FlatmapMerge:
Concurrently collects all the incoming flows and
merge their values into a single flow so that values
are emitted as soon as possible.
 */

fun flatMapMerge() {
    val startTime = System.currentTimeMillis()
    runBlocking {
        colorsFlow.flatMapMerge { color -> printColor(color) }.collect {
            println("$it ${System.currentTimeMillis() - startTime} ms from start")
        }
    }
}

/*
FlatmapLatest:
Collection of the previous flow is cancelled as soon
as new flow is emitted.
 */
fun flatMapLatest() {
    val startTime = System.currentTimeMillis()
    runBlocking {
        colorsFlow.flatMapLatest { color -> printColor(color) }.collect {
            println("$it ${System.currentTimeMillis() - startTime} ms from start")
        }
    }
}
