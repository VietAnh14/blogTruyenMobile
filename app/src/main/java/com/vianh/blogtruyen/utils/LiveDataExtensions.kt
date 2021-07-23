package com.vianh.blogtruyen.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

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