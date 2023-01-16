package com.saket.kotlinflows.coreproperties

import java.lang.Exception
import java.lang.IllegalStateException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
A flow collector can use a try/catch block to catch exceptions. This catches any
exceptions that happens in the emitter or any intermediate or terminal operators.
 */
fun useTryCatchInFlowCollector() {
    // Strangely, this does not work with launchIn operator..
    /*
    simpleTestFlow()
    .onEach { println(it) }
    .launchIn(CoroutineScope(Dispatchers.Default))
     */
    CoroutineScope(Dispatchers.Default).launch {
        try {
            simpleTestFlow().collect { println(it) }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

fun simpleTestFlow() = flow {
    repeat(10) {
        delay(500)
        if (it < 5) {
            emit("Flow value is $it")
        } else {
            throw IllegalStateException()
        }
    }
}

/*
Exception transparency:
Flows must be transparent to exceptions and it is a violation of the exception transparency
to emit values in the flow { ... } builder from inside of a try/catch block.

The emitter can use a catch operator that preserves this exception transparency and allows
encapsulation of its exception handling.
The body of the catch operator can analyze an exception and react to it in different ways
depending on which exception was caught:

Exceptions can be rethrown using throw.
Exceptions can be turned into emission of values using emit from the body of catch.
Exceptions can be ignored, logged, or processed by some other code.
 */
fun useCatchOperatorToHandleException() {
    runBlocking {
        simpleTestFlow().catch { e -> emit("Caught $e") }.collect { value -> println(value) }
    }
}

/*
Transparent catch:
The catch intermediate operator, honoring exception transparency, catches only upstream exceptions
(that is an exception from all the operators above catch, but not below it).
If the block in collect { ... } (placed below catch) throws an exception then it escapes
 */

/*
Catching declaratively:
We can combine the declarative nature of the catch operator with a desire to handle all the exceptions,
by moving the body of the collect operator into onEach and putting it before the catch operator.
Collection of this flow must be triggered by a call to collect() without parameters:
 */
fun catchDeclaratively() {
    CoroutineScope(Dispatchers.Default).launch {
        simpleTestFlow()
            .onEach { value ->
                val currInt = Integer.parseInt(value)
                check(currInt <= 2) { println("Collected $value") }
                println(value)
            }
            .catch { ex -> println("Caught $ex") }
            .collect()
    }
}
