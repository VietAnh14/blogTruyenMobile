package com.vianh.blogtruyen.utils

import android.content.Context
import android.net.Uri
import com.vianh.blogtruyen.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import kotlin.math.max

fun getDeviceHeight(context: Context): Int {
    return context.resources.displayMetrics.heightPixels
}

fun getDeviceWidth(context: Context): Int {
    return context.resources.displayMetrics.widthPixels
}
class BlogTruyenInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.header("Referer") != null) {
            return chain.proceed(request)
        } else {
            val newRequest = request.newBuilder()
                .header("Referer", BuildConfig.HOST).build()

            return chain.proceed(newRequest)
        }
    }

}

fun getMaxTextureSize(): Int {

    // Safe minimum default size
    val IMAGE_MAX_BITMAP_DIMENSION = 2048

    // Get EGL Display
    val egl = EGLContext.getEGL() as EGL10
    val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)

    // Initialise
    val version = IntArray(2)
    egl.eglInitialize(display, version)

    // Query total number of configurations
    val totalConfigurations = IntArray(1)
    egl.eglGetConfigs(display, null, 0, totalConfigurations)

    // Query actual list configurations
    val configurationsList = arrayOfNulls<EGLConfig>(totalConfigurations[0])
    egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations)

    val textureSize = IntArray(1)
    var maximumTextureSize = 0

    // Iterate through all the configurations to located the maximum texture size
    for (i in 0 until totalConfigurations[0]) {
        // Only need to check for width since opengl textures are always squared
        egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize)

        // Keep track of the maximum texture size
        if (maximumTextureSize < textureSize[0]) maximumTextureSize = textureSize[0]
    }

    // Release
    egl.eglTerminate(display)

    // Return largest texture size found, or default
    return max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION)
}

fun getMimeType(uri: Uri, context: Context): String? {
    return context.contentResolver.getType(uri)?.toLowerCase(Locale.ENGLISH)
}