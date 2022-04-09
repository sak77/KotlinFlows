package com.saket.kotlinflows

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking

/*
Context preservation property of flows:
Collection of flows always happens in the context
of the calling coroutine.

The issue with withContext():
withContext() can be used to change the context
for Coroutines. However the flow builder has
to honor the context preservation property and
is not allowed to emit from a different context.

To fix this, we use the flowOn() operator:
flowOn() operator allows to change the context
of the flow emission. While context of collection
of flow still happens by the calling function.

flowOn operator creates another coroutine for upstream
flow when it has to change CoroutineDispatcher in
its context.
 */

fun testFlowOnOperator() {
    runBlocking { cityFlow().collect { println("collecting $it on ${Thread.currentThread()}") } }
}

private fun cityFlow(): Flow<String> {
    val cities = listOf("Mumbai", "Delhi", "Bangalore", "Pune", "Kolkata")
    return flow {
            for (city in cities) {
                Thread.sleep(1000)
                println("emit $city on ${Thread.currentThread()}")
                emit(city)
            }
        }
        .flowOn(Dispatchers.Default)
}
