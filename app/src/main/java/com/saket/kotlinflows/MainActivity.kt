package com.saket.kotlinflows

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

/**
 * The purpose of this app is to explore Kotlin flows.
 *
 * Flow builders
 * Flow Collectors
 * Intermediate methods
 * Transforms
 * Combining flows
 *
 * Flows vs Coroutines
 * Flows and reactive streams
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mySimpleList = simpleList()

        //Print values from a simple list
        /*
        mySimpleList.forEach { println(it) }
         */

        val mySimpleSequence = simpleSequence()
        //Print values from a sequence
        /*
        mySimpleSequence.forEach { println(it) }
         */

        //Print value from suspended function
        /*
                println("Before runBlocking")
        runBlocking {
            simpleSuspendedList().forEach {
                    value ->
                println(value)
            }
        }
        println("After runBlocking")
         */

        //Collect and print values from flow
        /*
                runBlocking {
            // Launch a concurrent coroutine to check if
            // the main thread is blocked
            launch {
                for (k in 1..3) {
                    println("I'm not blocked $k")
                    delay(100)
                }
            }
            simpleFlow().collect {
                println(it)
            }
        }
         */

        //Printing values from a limited flow
        /*
        runBlocking {
            myLimitedFLow.collect {
                println(it)
            }
        }
         */

        //Convert list to flow
        /*
        runBlocking {
            listOfNames.asFlow().collect {
                println(it)
            }
        }
         */

        /*
        Flows provide many intermediate operators
        which can transform the emits. Here i look
        at some common ones like map, filter and transform.
         */
        //applyMap()
        //transformOperator()

        /*
        Terminal flow operators:
        collect() is the most basic one.
        toList(), toSet(): convert to collections.
        first, single operators.
        reduce, fold etc. Refer the same link as above for more:
        https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/index.html
         */
        /*
        runBlocking {
            val sum = (1..5).asFlow()
                .map { it * it } // squares of numbers from 1 to 5
                .reduce { a, b -> a + b } // sum them (terminal operator)
            println(sum)
        }
         */

        /*
        Flows are sequential.
        Each individual collection of a flow is performed
        sequentially unless special operators that operate
        on multiple flows are used.
         */
        /*
                runBlocking {
            mySimpleList.asFlow()
                .filter {
                    it % 2 == 0
                }
                .map { "string $it" }
                .collect {
                    println("Collect $it")
                }
        }
         */

        /*
        Context preservation using flowOn operator.
         */
        //testFlowOnOperator()

        /*
        Buffering is an intermediate operator that
        tells flow to emit and collect values
        concurrently instead of sequentially.
         */
        //testFlowBuffering()

        /*
        Conflation mean skipping some intermediate emissions.
        This can be used when collector is too slow to
        process all emissions and will instead process the
        most recent ones only.
         */
        //testFlowConflation()

        //combineFlows()

        //flatmapConcat()

        //flatMapMerge()

        //flatMapLatest()
    }

    private fun simpleList() = arrayOf(1, 2, 3)

    /*
    A collection that perform some computation to
    generate each value, can be a Sequence.
    Sequence type represents lazily evaluated collections.
     */
    private fun simpleSequence(): Sequence<Int> = sequence {
        for (i in 1..3) {
            Thread.sleep(100)   //Some computation
            yield(i)
        }
    }

    /*
    Sequence computation blocks the main thread.
    To perform computation asynchronously,
    use a suspended function. Note, that this fun returns
    the entire list as a single value.
     */
    private suspend fun simpleSuspendedList(): List<Int> {
        delay(1000)  //Some computation
        return listOf(1, 2, 3, 4)
    }

    /*
    Above, using the List<Int> result type,
    means we can only return all the values at once.
    To represent the stream of values that are being
    asynchronously computed, we can use a Flow<Int> type
    just like we would use the Sequence<Int> type for
    synchronously computed values.
     */
    private fun simpleFlow(): Flow<Int> {
        return flow {   //Flow builder
            //Code inside the flow { ... } builder block can suspend.
            for (i in 1..3) {
                delay(1000) //Some async computation
                //if we use Thread.sleep() instead, it will block the main thread.
                emit(i) //emit next value
            }
        }
    }

    /*
    Other flow builders-
    flowOf() - To emit limited set of values.
    asFlow() - To convert a collection to a flow.
     */
    private val myLimitedFLow = flowOf(1, 3, 4)
    private val listOfNames = listOf("Red", "Blue", "Orange", "Green")
}