package com.vianh.blogtruyen.utils

import okhttp3.Call
import okhttp3.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HttpError(private val response: Response): Exception() {
    override val message: String?
        get() = "Request response with code: ${response.code}, message: ${response.message}"
    fun getErrorCode(): Int {
        return response.code
    }
}

suspend fun <T> Call.suspendGetResult(): T {
    return suspendCoroutine {
        val response = execute()
        if (response.isSuccessful && response.body != null) {
            it.resume(response.body?.string() as T)
        } else {
            it.resumeWithException(HttpError(response))
        }
    }
}

fun Call.extractData(): String {
    val response = execute()
    if (response.isSuccessful && response.body != null) {
        return response.body!!.string()
    } else {
        throw HttpError(response)
    }
}

