package com.vianh.blogtruyen.utils

import android.view.Gravity
import android.view.View
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

fun View.toggleState(direction: Int) {
    if (visibility == View.GONE) {
        visibility = View.VISIBLE
        animate().setDuration(200).translationY(0f)
    } else {
        when (direction) {
            Gravity.TOP -> animate().setDuration(200).translationY(-height.toFloat()).withEndAction {
                visibility = View.GONE
            }
            Gravity.BOTTOM -> animate().setDuration(200).translationY(height.toFloat()).withEndAction {
                visibility = View.GONE
            }
        }
    }
}