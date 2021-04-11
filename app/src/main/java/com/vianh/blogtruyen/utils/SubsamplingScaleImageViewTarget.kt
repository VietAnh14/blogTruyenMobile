package com.vianh.blogtruyen.utils

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.ui.reader.PageLoadCallBack

class SubsamplingScaleImageViewTarget<R>(val view: SubsamplingScaleImageView, val pageLoadCallBack: PageLoadCallBack<R>)
    : CustomViewTarget<SubsamplingScaleImageView, R>(view) {
    override fun onLoadFailed(errorDrawable: Drawable?) {
        pageLoadCallBack.onLoadFailed(errorDrawable)
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        pageLoadCallBack.onLoadCleared(placeholder)
    }

    override fun onResourceReady(resource: R, transition: Transition<in R>?) {
        pageLoadCallBack.onResourceReady(resource, transition)
    }
}