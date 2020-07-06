package com.vianh.blogtruyen.utils

import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.ui.mangaViewer.PageLoadCallBack
import java.io.File

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