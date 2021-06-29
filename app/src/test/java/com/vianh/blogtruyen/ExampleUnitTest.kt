package com.vianh.blogtruyen

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test

import org.junit.Assert.*
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // pretend we are doing something useful here
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // pretend we are doing something useful here, too
        return 29
    }

    @Test
    fun testCoroutine() {
        runBlocking {
            val time = measureTimeMillis {
                val one = async { doSomethingUsefulOne() }
                val two = async { doSomethingUsefulTwo() }
                // some computation
//                one.start() // start the first one
//                two.start() // start the second one
                print("one ${one.isCompleted}")
                println("The answer is ${one.await() + two.await()}")
            }
            println("Completed in $time ms")
        }
    }

    // The result type of somethingUsefulOneAsync is Deferred<Int>
    fun somethingUsefulOneAsync() = GlobalScope.async {
        doSomethingUsefulOne()
    }

    // The result type of somethingUsefulTwoAsync is Deferred<Int>
    fun somethingUsefulTwoAsync() = GlobalScope.async {
        doSomethingUsefulTwo()
    }

    @Test
    fun main() {
        val time = measureTimeMillis {
            // we can initiate async actions outside of a coroutine
            val one = somethingUsefulOneAsync()
            val two = somethingUsefulTwoAsync()
            // but waiting for a result must involve either suspending or blocking.
            // here we use `runBlocking { ... }` to block the main thread while waiting for the result
            runBlocking {
                println("The answer is ${one.await() + two.await()}")
            }
        }
        println("Completed in $time ms")
    }

    @ObsoleteCoroutinesApi
    @Test
    fun testFlow() {
        val stateFlowA = MutableStateFlow(1)
        val stateFlowB = MutableStateFlow("a")

        val combineFlow = stateFlowA.combine(stateFlowB) { a, b ->
            println("emitting thread: ${Thread.currentThread().name}")
            a * a * a * a
        }.flowOn(newSingleThreadContext("Some thread"))
            .map {
                println("map thread: ${Thread.currentThread().name}")
                it * it
            }

        GlobalScope.launch {
            combineFlow.collect {
                println("collect $it     thread: ${Thread.currentThread().name}")
            }
        }

        runBlocking {
            stateFlowA.value = 2
            delay(200)
            stateFlowB.value = "v"
            delay(1000)
            stateFlowA.value = 12
            delay(1000)
            stateFlowA.value= 10

        }
    }
}
