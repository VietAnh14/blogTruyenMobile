package com.vianh.blogtruyen.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

// To not confuse with kotlin result type
typealias MonadResult<T> = Result<T, Throwable>

fun <T, E: Throwable> Result<T, E>.getOrThrow(): T {
    return when (this) {
        is Ok -> this.value
        is Err -> throw this.error
        else -> throw IllegalArgumentException("Unknown result type")
    }
}