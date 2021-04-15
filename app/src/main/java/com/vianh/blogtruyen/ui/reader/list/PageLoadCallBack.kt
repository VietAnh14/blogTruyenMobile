package com.vianh.blogtruyen.ui.reader.list

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.transition.Transition

interface PageLoadCallBack<R> {
    fun onResourceReady(resource: R, transition: Transition<in R>?)
    fun onLoadFailed(errorDrawable: Drawable?)
    fun onLoadCleared(placeholder: Drawable?)
}