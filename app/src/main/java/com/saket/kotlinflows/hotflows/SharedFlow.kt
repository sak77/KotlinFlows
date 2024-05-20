package com.saket.kotlinflows.hotflows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Shares the same flow for all its collectors. A hot flow which exists independently
 * of any active collectors. An active collector of a shared flow is called a subscriber.
 * It shares emitted values among all its collectors in a broadcast fashion, so that
 * all collectors get all emitted values.
 *
 * Shared flow never completes normally. A subscriber of a shared flow can be cancelled.
 * This usually happens when the scope in which the coroutine is running is cancelled.
 * A subscriber to a shared flow is always cancellable, and checks for cancellation
 * before each emission.
 *
 * Note that most terminal operators like Flow.toList would also not complete, when
 * applied to a shared flow, but flow-truncating operators like Flow.take and
 * Flow.takeWhile can be used on a shared flow to turn it into a completing one.
 * SharedFlow is useful for broadcasting events that happen inside an application
 * to subscribers that can come and go.
 *
 * Replay cache and buffer - A sharedflow keeps a specific number of most recent values
 * in its replay cache. Every new subscriber first gets values from the replay cache and
 * then gets new emitted values. The max size of the replay cache is specified when a
 * shared flow is created by the replay parameter. A snapshot of the replay cache is
 * available via the replayCache property and it can be reset via
 * MutableStateFlow.resetReplayCache. Replay cache provides buffer for emissions to the
 * shared flow, allowing slow subscribers to get values from the buffer without
 * suspending emitters.
 *
 * A shared flow with a buffer can be configured to avoid suspension of emitters on
 * buffer overflow using the onBufferOverflow parameter, which can be set to either:
 * SUSPEND - upstream that is sending or emitting value is suspended while the buffer
 * is full. DROP_OLDEST - drop oldest value in buffer on overflow, add new elements to
 * the buffer, do not suspend. DROP_LATEST - drop latest value being added to the buffer
 * on overflow, so that content of buffer does not change but do not suspend. Buffer
 * overflow condition can happen only when there is at least one subscriber that is
 * not ready to accept the new value. In the absence of subscribers only the most recent
 * replay values are stored and the buffer overflow behavior is never triggered and has
 * no effect. In particular, in the absence of subscribers emitter never suspends despite
 * BufferOverflow.SUSPEND option and BufferOverflow.DROP_LATEST option does not have
 * effect either. Essentially, the behavior in the absence of subscribers is always
 * similar to BufferOverflow.DROP_OLDEST, but the buffer is just of replay size
 * (without any extraBufferCapacity).
 *
 * unbuffered shared flow - A default implementation of a shared flow that is created with
 * MutableSharedFlow() constructor function without parameters has no replay cache nor additional
 * buffer. emit call to such a shared flow suspends until all subscribers receive the emitted value
 * and returns immediately if there are no subscribers. Thus, tryEmit call succeeds and returns true
 * only if there are no subscribers (in which case the emitted value is immediately lost).
 */

/*
It is not possible to create instance of SharedFlow, since it does not expose a
constructor. So instead, one must create a MutableSharedFlow and call its
asSharedFlow method to get a SharedFlow instance.
 */
val _sampleFlow = MutableSharedFlow<String>()
val sampleFlow = _sampleFlow.asSharedFlow()

fun launchSharedFlow() {
    // Here mutableSharedFlow instance is the backing property for SharedFlow instance.
    CoroutineScope(Dispatchers.Default).launch {
        for (i in 1..50) {
            delay(100)
            println("Emitting $i")
            _sampleFlow.emit("$i")
        }
    }
}

/*
Unlike cold flows where the block is executed each time a
collector is added. Shared flow shares the same flow with
all its subscribers.
 */
fun sharedflow_shares_flow_with_multiple_subscribers() {
    launchSharedFlow()
    CoroutineScope(Dispatchers.Default).launch {
        launch { sampleFlow.collect { println("Subscriber 1: $it") } }
        /*
        2nd subscriber will get the same collection as 1st,
        so it may not receive some initial elements due to
        the delay.
         */
        delay(2000)
        launch { sampleFlow.collect { println("Subscriber 2: $it") } }
    }
}

/*
While Shared flow continues to emit values even after
collectors are cancelled. The cold flow stops emitting as
soon as the collectors cancel.
 */
fun shared_flow_exists_independent_of_subscribers() {
    launchSharedFlow()
    CoroutineScope(Dispatchers.Default).launch {
        val job1 = launch { sampleFlow.collect { println("Collecting shared flow: $it") } }
        delay(1000)
        // Shared flow continues to emit, despite cancelling the job.
        job1.cancel()
        // Cold flow
        val job2 = launch { simpleColdFlow().collect { println("Collecting cold flow: $it") } }
        delay(1000)
        job2.cancel()
    }
}

fun simpleColdFlow() = flow {
    for (i in 1..50) {
        delay(100)
        println("Cold Emit $i")
        // emit is a suspending function, so it must be called from a coroutine
        emit("Test Emit $i")
    }
}
