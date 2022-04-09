package com.saket.kotlinflows

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * The purpose of this app is to explore Kotlin flows.
 *
 * Flow builders Flow Collectors Intermediate methods Transforms Combining flows
 *
 * Flows vs Coroutines Flows and reactive streams
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testFlowBuffering()
    }

    fun test_coldflow_builders() {
        /*
        1. Regular way of collecting flows...
         */
        /*
        CoroutineScope(Dispatchers.Default).launch {
            builder_flow().collect {
                println("1st collector $it")
            }

            delay(3000)
            /*
            Each time a cold flow is collected, the block is
            called and sends the entire flow to the collector.
             */
            builder_flow().collect {
                println("2nd collector $it")
            }
            delay(3000)
            builder_flow().collect {
                println("3rd collector $it")
            }
        }
         */

        /*
        2. Shorthand way of collecting flows using launchIn.
         */
        /*
        builder_asFlow()
            .onEach { delay(1000) }
            .onEach { println(it) }
            .launchIn(CoroutineScope(Dispatchers.Default))
         */

        /*
         */
        builder_channelFlow().onEach { println(it) }.launchIn(CoroutineScope(Dispatchers.Default))

        /*
        builder_callbackFlow<String>()
            .launchIn(CoroutineScope(Dispatchers.Default))
         */
    }
}
