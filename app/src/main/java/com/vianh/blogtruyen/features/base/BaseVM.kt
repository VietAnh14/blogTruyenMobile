package com.vianh.blogtruyen.features.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.utils.SingleLiveEvent
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseVM: ViewModel() {
    val isLoading = SingleLiveEvent<Boolean>()
    val error = SingleLiveEvent<Throwable>()
    val toast = SingleLiveEvent<String>()

    fun launchLoading(
        coroutineContext: CoroutineContext = EmptyCoroutineContext,
        coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
        errorHandler: CoroutineExceptionHandler = createExceptionHandler(),
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(coroutineContext + errorHandler, coroutineStart) {
            try {
                isLoading.postValue(true)
                block()
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun launchJob(
        coroutineContext: CoroutineContext = EmptyCoroutineContext,
        coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
        errorHandler: CoroutineExceptionHandler = createExceptionHandler(),
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(coroutineContext + errorHandler, coroutineStart, block)

    protected open fun createExceptionHandler() =
        CoroutineExceptionHandler { _, throwable ->
            if (throwable !is CancellationException) {
                Timber.e(throwable)
                error.postValue(throwable)
            }
        }
}