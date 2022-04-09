package com.saket.kotlinflows

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.delay

/**
 * When using LiveData, you might need to calculate values asynchronously. You can use the liveData
 * builder function to call a suspend function, serving the result as a LiveData object.
 */
var user: LiveData<User> = liveData {
    delay(2000) // Prepare response user..
    val responseUser = User("Saket", 1)
    emit(responseUser)
}

/*
The liveData building block serves as a structured concurrency primitive between coroutines and LiveData.
The code block starts executing when LiveData becomes active and is automatically canceled after
a configurable timeout when the LiveData becomes inactive. If it is canceled before completion,
it is restarted if the LiveData becomes active again. If it completed successfully in a previous
run, it doesn't restart.
 */

/*
Note: it is restarted only if canceled automatically. If the block is canceled for any other reason
(e.g. throwing a CancellationException), it is not restarted.
 */

/*
https://developer.android.com/topic/libraries/architecture/coroutines#livedata
 */

class User(val name: String, val id: Int)
