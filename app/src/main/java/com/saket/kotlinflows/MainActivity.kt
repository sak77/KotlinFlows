package com.saket.kotlinflows

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.saket.kotlinflows.builders.builder_channelFlow
import com.saket.kotlinflows.builders.builder_flow
import com.saket.kotlinflows.coreproperties.catchDeclaratively
import com.saket.kotlinflows.coreproperties.testFlowOnOperator
import com.saket.kotlinflows.hotflows.DownloadStatus
import com.saket.kotlinflows.hotflows.mockDataDownload
import com.saket.kotlinflows.hotflows.state
import com.saket.kotlinflows.operators.combineFlows
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //catchDeclaratively()
        //test_coldflow_builders()
        testFlowOnOperator()
        //combineFlows()
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

    /*
    collect can be called more than once inside a coroutine.
    But if we are collecting a stateflow, it appears we can only collect once per coroutine.
     */
    fun collect_stateflow() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    state.collect {
                        println("First COllector Download status $it")
                    }
                }

                launch {
                    state.collect {
                        println("Second COllector Download status $it")
                    }
                }
            }
        }
        mockDataDownload()
    }

}
