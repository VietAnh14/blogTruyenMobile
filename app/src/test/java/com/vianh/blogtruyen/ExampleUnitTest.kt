package com.vianh.blogtruyen

import com.vianh.blogtruyen.utils.cancelableCatching
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

    val exHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Coroutine handler catch an exception ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.IO)

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // pretend we are doing something useful here
        throw IllegalArgumentException("Oops got an exception here!!!")
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(2000L) // pretend we are doing something useful here, too
        println("Done 2")
        return 29
    }

//    @Test
//    fun testCoroutine() {
//        runBlocking {
//            val time = measureTimeMillis {
//                val one = async { doSomethingUsefulOne() }
//                val two = async { doSomethingUsefulTwo() }
//                // some computation
////                one.start() // start the first one
////                two.start() // start the second one
//                print("one ${one.isCompleted}")
//                println("The answer is ${one.await() + two.await()}")
//            }
//            println("Completed in $time ms")
//        }
//    }

    // The result type of somethingUsefulOneAsync is Deferred<Int>
    fun somethingUsefulOneAsync() = scope.async {
        doSomethingUsefulOne()
    }

    // The result type of somethingUsefulTwoAsync is Deferred<Int>
    fun somethingUsefulTwoAsync() = scope.async {
        doSomethingUsefulTwo()
    }

    // Async in root scope throw exception normally
    // Async in launch throw exception event when we catch them
    // Using coroutineScope and it throw exception normally cause coroutine scope cancel all child
    // and rethrow any exceptions
    // Or we could use withContext block and it fail as expected
//    @Test
//    fun testException() {
//        val job = scope.launch(exHandler) {
//            println("Run test")
//            try {
//                supervisorScope {
//                    launch { doSomethingUsefulTwo() }
//                    launch { doSomethingUsefulOne() }
//                    println("I'm still alive")
//                }
//            } catch (e: Exception) {
//                println("I'm dead by now ${e.message}")
//            }
//        }
//        runBlocking {
//            job.join()
//        }
//    }

//    @Test
//    fun main() {
//        val time = measureTimeMillis {
//            // we can initiate async actions outside of a coroutine
//            val one = somethingUsefulOneAsync()
//            val two = somethingUsefulTwoAsync()
//            // but waiting for a result must involve either suspending or blocking.
//            // here we use `runBlocking { ... }` to block the main thread while waiting for the result
//            runBlocking {
//                println("The answer is ${one.await() + two.await()}")
//            }
//        }
//        println("Completed in $time ms")
//    }

//    @ObsoleteCoroutinesApi
//    @Test
//    fun testFlow() {
//        val flowA = flow {
//            cancelableCatching {
//                emit(1)
//                delay(1000)
//                emit(100)
//                delay(2000)
//                throw CancellationException()
//                emit(1000)
//            }
//        }
//
//        val job = flowA
//            .catch {
//                emit(-200)
//                println("Catch err 2 $it")
//            }
//            .onCompletion {
//                println("Complete $it")
//            }
//            .onEach { println("On each $it") }
//            .launchIn(GlobalScope)
//
//
//        runBlocking {
//            delay(1500)
////            job.cancel()
//            delay(3000)
//        }
//    }
}
