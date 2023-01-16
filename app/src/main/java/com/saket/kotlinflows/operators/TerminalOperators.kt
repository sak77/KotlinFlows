package com.saket.kotlinflows.operators

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
