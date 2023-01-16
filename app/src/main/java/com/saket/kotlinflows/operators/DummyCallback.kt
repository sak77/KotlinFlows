package com.saket.kotlinflows.operators

/*
For callback flows..
 */
abstract class DummyCallback<T> {
    abstract fun onNextValue(value: T)
    abstract fun onApiError(cause: Throwable)
    abstract fun onCompleted(): Boolean
}
