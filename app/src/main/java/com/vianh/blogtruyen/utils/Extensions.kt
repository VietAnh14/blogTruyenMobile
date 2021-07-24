package com.vianh.blogtruyen.utils

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HttpError(private val code: Int, message: String?) : Exception(message) {
    override val message: String?
        get() = "Request response with code: ${code}, message: ${super.message}"

    fun getErrorCode(): Int {
        return code
    }
}

suspend fun Call.getBodyString(): String {
    return suspendCancellableCoroutine {
        it.invokeOnCancellation {
            this.cancel()
        }

        this.enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                it.resume(response.body!!.string())
            }
        })
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

suspend fun RequestManager.preloadSuspend(uri: String) {
    suspendCancellableCoroutine<Unit> {
        this.asFile()
            .load(uri)
            .listener(object: RequestListener<File> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<File>?,
                    isFirstResource: Boolean
                ): Boolean {
                    it.resume(Unit)
                    return false
                }

                override fun onResourceReady(
                    resource: File?,
                    model: Any?,
                    target: Target<File>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    it.resume(Unit)
                    return false
                }
            }).submit()
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

fun View.slideDown(duration: Long = 400, interpolator: Interpolator = AccelerateInterpolator()) {
    animate()
        .translationY(height.toFloat())
        .setInterpolator(interpolator)
        .setDuration(duration)
        .start()
}

fun View.slideUp(duration: Long = 400, interpolator: Interpolator = AccelerateInterpolator()) {
    animate()
        .translationY(height * -1f)
        .setInterpolator(interpolator)
        .setDuration(duration)
        .start()
}

fun View.resetPos(duration: Long = 400, interpolator: Interpolator = AccelerateInterpolator()) {
    animate()
        .translationY(0f)
        .setInterpolator(interpolator)
        .setDuration(duration)
        .start()
}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}