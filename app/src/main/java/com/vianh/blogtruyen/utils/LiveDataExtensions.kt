package com.vianh.blogtruyen.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

fun <T> Flow<T>.observeAsLiveData(owner: LifecycleOwner, observer: Observer<T>) {
    this.asLiveData().observe(owner, observer)
}

inline fun <T> Flow<List<T>>.ifEmpty(crossinline block: suspend () -> List<T>): Flow<List<T>> {
    return transform { value ->
        if (value.isEmpty())
            emit(block.invoke())
        else
            emit(value)
    }
}