package com.vianh.blogtruyen.utils

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.vianh.blogtruyen.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
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
                .header("Referer", request.url.host).build()

            return chain.proceed(newRequest)
        }
    }

}

fun hideSystemUI(window: Window) {
    // Enables regular immersive mode.
    // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
    // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            // Set the content to appear under the system bars so that the
            // content doesn't resize when the system bars hide and show.
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // Hide the nav bar and status bar
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

// Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
fun showSystemUI(window: Window) {
    //Magic color that make status bar transparent (arbg) first 4 bits are alpha
    window.statusBarColor = Color.parseColor("#99000000")
    window.navigationBarColor = Color.parseColor("#99000000")
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
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

fun ImageView.loadNetWorkImage(url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}