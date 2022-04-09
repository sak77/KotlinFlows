package com.saket.kotlinflows

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * Flow builders create a cold flow from a suspending block.
 *
 * Since its a cold flow, the block is called everytime a terminal operator is applied to the flow
 * and is started separately for each collector.
 */

/*
Flow builders can be defined outside a coroutine/suspend function.
But collection has to happen in a suspending function.
 */
fun builder_flow(): Flow<String> = flow {
    delay(300)
    emit("Simple String 1")
    delay(300)
    emit("Simple String 2")
    delay(300)
    emit("Simple String 3")
}

fun builder_flowOf(): Flow<String> =
    flowOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

fun builder_asFlow(): Flow<String> =
    listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday").asFlow()

/*
produces cold flows.
Uses Producer to send data to subscribers.
channelFlow builder ensures thread-safety and
context preservation, so it allows to send data produced by code
blocks running concurrently in different contexts.
it returns a FLow that completes as soon as the code in the block and
all its children completes.
Use awaitClose as the last statement to keep flow running until
consumer cancels flow collection, or the
callback based api calls SendChannel.close.
 */
fun builder_channelFlow() = channelFlow {
    // Launch coroutine to send data from one coroutine
    launch(Dispatchers.Default) {
        for (i in 0..5) {
            delay(1000)
            // For long running tasks check if send channel not closed
            if (!isClosedForSend) {
                send(i)
            }
        }
    }
    // Send data from another coroutine, concurrently
    launch(Dispatchers.IO) {
        delay(3000) // simulate network response...
        send(45)
    }

    // Simulate close channel by api
    delay(4000)
    trySend(close())

    awaitClose {
        // Clean up references..this prevents memory leaks
        // after the flow has stopped..
        println("Cleaning up")
    }
}

/*
A special type of channel flow.
Callback flow acts as a wrapper around a callback implementation which
sends messages downstream for different callback methods invoked.
it is similar to channel flow builder in that it ensures thread-safety and
context preservation, so ProducerScope can be used from any context.
Resulting flow completes as soon as the block completes. awaitClose should be
used to keep the flow running.

awaitClose argument is called either when a flow consumer cancels the flow collection
or when a callback-based API invokes SendChannel.close manually and is typically used
to cleanup the resources after the completion, e.g. unregister a callback.

A channel with the default buffer size is used. Use the buffer operator on the resulting
flow to specify a user-defined value and to control what happens when data is produced
faster than consumed, i.e. to control the back-pressure behavior.
 */
fun <T> builder_callbackFlow() = callbackFlow {
    val callback =
        object : DummyCallback<T>() { // Implementation of some callback interface
            override fun onNextValue(value: T) {
                // To avoid blocking you can configure channel capacity using
                // either buffer(Channel.CONFLATED) or buffer(Channel.UNLIMITED) to avoid overfill
                trySendBlocking(value).onFailure { throwable ->
                    // Downstream has been cancelled or failed, can log here
                }
            }

            override fun onApiError(cause: Throwable) {
                NonCancellable.cancel(CancellationException("API Error", cause))
            }

            override fun onCompleted() = channel.close()
        }

    // api.register(callback)

    // simulate callback events
    // callback.onNextValue(T)

    /*
     * Suspends until either 'onCompleted'/'onApiError' from the callback is invoked
     * or flow collector is cancelled (e.g. by 'take(1)' or because a collector's coroutine was cancelled).
     * In both cases, callback will be properly unregistered.
     */
    awaitClose { // api.unregister(callback)
    }
}

fun builder_emptyFlow(): Flow<String> = emptyFlow()
