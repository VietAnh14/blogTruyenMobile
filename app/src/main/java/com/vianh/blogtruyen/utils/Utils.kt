package com.vianh.blogtruyen.utils

import android.content.Context
import com.vianh.blogtruyen.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

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
//fun dpToPx() {
//
//}