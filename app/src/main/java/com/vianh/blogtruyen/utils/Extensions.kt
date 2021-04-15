package com.vianh.blogtruyen.utils

import android.view.Gravity
import android.view.View
import okhttp3.Call
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HttpError(private val code: Int, message: String?) : Exception(message) {
    override val message: String?
        get() = "Request response with code: ${code}, message: ${super.message}"

    fun getErrorCode(): Int {
        return code
    }
}

suspend fun <T> Call.suspendGetResult(): T {
    return suspendCoroutine {
        val response = execute()
        if (response.isSuccessful && response.body != null) {
            it.resume(response.body?.string() as T)
        } else {
            it.resumeWithException(HttpError(response.code, response.message))
        }
        response.body?.close()
    }
}

fun Call.extractData(): String {
    val response = execute()
    try {
        if (response.isSuccessful && response.body != null) {
            return response.body!!.string()
        } else {
            throw HttpError(response.code, response.message)
        }
    } finally {
        response.close()
    }
}

fun View.toggleState(direction: Int) {
    if (visibility == View.GONE) {
        visibility = View.VISIBLE
        animate().setDuration(200).translationY(0f)
    } else {
        when (direction) {
            Gravity.TOP -> animate().setDuration(200).translationY(-height.toFloat())
                .withEndAction {
                    visibility = View.GONE
                }
            Gravity.BOTTOM -> animate().setDuration(200).translationY(height.toFloat())
                .withEndAction {
                    visibility = View.GONE
                }
        }
    }
}

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}