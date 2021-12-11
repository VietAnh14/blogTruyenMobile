package com.vianh.blogtruyen.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

inline fun <T> Flow<List<T>>.ifEmpty(crossinline block: suspend () -> List<T>): Flow<List<T>> {
    return transform { value ->
        if (value.isEmpty())
            emit(block.invoke())
        else
            emit(value)
    }
}

inline fun <T, R> Flow<List<T>>.mapList(crossinline transform: (T) -> R): Flow<List<R>> {
    return map { items ->
        items.map { transform.invoke(it) }
    }
}

inline fun <T> Flow<T>.asLiveDataDistinct(context: CoroutineContext, start: T): LiveData<T> {
    return this.onStart { emit(start) }.distinctUntilChanged().asLiveData(context)
}

inline fun <T> Flow<T>.asLiveDataDistinct(context: CoroutineContext): LiveData<T> {
    return this.distinctUntilChanged().asLiveData(context)
}

inline fun <T> Flow<T>.withPrevious(crossinline block: suspend (prev: T?, new: T) -> Unit): Flow<T> {
    var prev: T? = null

    return onEach {
        block.invoke(prev, it)
        prev = it
    }
}