package com.vianh.blogtruyen.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.utils.SingleLiveEvent
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel(val dataManager: DataManager) : ViewModel() {
    val TAG = this::class.java.simpleName

    // SupervisorJob won't cancel all child coroutines when a coroutine throw an exception
    val jobs = SupervisorJob()
    val uiScope = CoroutineScope(Dispatchers.Main + jobs)
    val ioScope = CoroutineScope(Dispatchers.IO + jobs)
    val isLoading = MutableLiveData(false)
    fun isLoading(): LiveData<Boolean> = isLoading
    protected val error = SingleLiveEvent<Throwable>()
    fun error(): LiveData<Throwable> = error

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

    protected fun createExceptionHandler() =
        CoroutineExceptionHandler { _, throwable ->
            if (throwable !is CancellationException) {
                error.postValue(throwable)
            }
        }

    override fun onCleared() {
        super.onCleared()
        jobs.cancel()
    }
}