package com.saket.kotlinflows.hotflows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * StateFlow is a special type of SharedFlow whose replay cache holds only the latest emitted value.
 * This value can be used to represent current state of an object in the application.
 *
 * The emissions from a stateFlow are conflated, which means that if subscriber is slow it can skip
 * fast updates, but always collects the latest emitted value from the flow. StateFlow always has an
 * initial value. A state flow behaves identically to a shared flow when it is created with the
 * following parameters and the distinctUntilChanged operator is applied to it: //
 * MutableStateFlow(initialValue) is a shared flow with the following parameters: val shared =
 * MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
 * shared.tryEmit(initialValue) // emit the initial value val state = shared.distinctUntilChanged()
 * // get StateFlow-like behavior
 */

/*
Represents current downloading status
 */
enum class DownloadStatus {
    NOT_REQUESTED,
    INITIALIZED,
    IN_PROGRESS,
    SUCCESS
}

val _state = MutableStateFlow<DownloadStatus>(DownloadStatus.NOT_REQUESTED)
val state: StateFlow<DownloadStatus>
    get() = _state

/*
The state is represented by the value. Any update to the value will
be reflected in all flow collectors by emitting a value with state
updates.

Value does not have to be set in a suspended function.
 */
fun mockDataDownload() {
    // initializeConnection()
    _state.value = DownloadStatus.INITIALIZED
    /*
    Downloading partial data...
     */
    Thread.sleep(2000)
    _state.value = DownloadStatus.IN_PROGRESS
    // Download completed
    Thread.sleep(2000)
    _state.value = DownloadStatus.SUCCESS
}

fun read_state_from_state_flow() {
    CoroutineScope(Dispatchers.Default).launch { state.collect { println(it.name) } }
    mockDataDownload()
}
