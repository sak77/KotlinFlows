package com.saket.kotlinflows

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

private val listOfNames = listOf("Red", "Blue", "Orange", "Green")
private fun simpleFlow() = flow {
    for (i in 1..3) {
        delay(1000) //Some async computation
        emit(i) //emit next value
    }
}

/*
    Composing Flows using zip operator, which
    takes 2 flows and applies a function and emits
    the combined elements
 */
fun zipFlows() {
    runBlocking {
        val flowOfNames = listOfNames.asFlow()
        simpleFlow().zip(flowOfNames) { number, name ->
            "$name -> $number"
        }
            .collect {
                println(it)
            }
    }
}

/*
    Combine operator will emit each element from
    first flow with latest emit from second flow.
 */
fun combineFlows() {
    runBlocking {
        val nums = simpleFlow().onEach { delay(300) }
        val names = listOfNames.asFlow().onEach { delay(400) }
        val startTime = System.currentTimeMillis()
        nums.combine(names) { number, name ->
            "$number -> $name"
        }
            .collect {
                println("$it at ${System.currentTimeMillis() - startTime} ms from start")
            }
    }
}
