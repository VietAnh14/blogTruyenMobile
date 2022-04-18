package com.vianh.blogtruyen.utils.ext

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException
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

fun Response.copyTo(to: File) {
    val stream = body?.byteStream() ?: return
    val sink = to.sink().buffer()
    sink.writeAll(stream.source())
    sink.close()
}

suspend fun Call.await(): Response {
    return suspendCancellableCoroutine {
        it.invokeOnCancellation {
            this.cancel()
        }

        this.enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                it.resume(response)
            }
        })
    }
}

inline fun <T> cancelableCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block.invoke())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun <T> RequestBuilder<T>.await(uri: String): T? {
    return suspendCoroutine {
        load(uri)
            .listener(object: RequestListener<T> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<T>?,
                    isFirstResource: Boolean
                ): Boolean {
                    it.resume(null)
                    return false
                }

                override fun onResourceReady(
                    resource: T,
                    model: Any?,
                    target: Target<T>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    it.resume(resource)
                    return false
                }

            }).submit()
    }
}

fun String.toSafeFileName(): String {
    return this.replace(Regex.fromLiteral("[^a-zA-Z0-9\\.\\-]"), "_")
}

fun File.createDirs(): File {
    if (!exists())
        mkdirs()
    return this
}

inline fun <T, R> Iterable<T>.mapToSet(transform: (T) -> R): Set<R> {
    return mapTo(HashSet<R>(), transform)
}