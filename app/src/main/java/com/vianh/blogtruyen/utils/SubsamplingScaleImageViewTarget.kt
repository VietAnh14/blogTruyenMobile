package com.vianh.blogtruyen.utils

import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.R
import java.io.File

class SubsamplingScaleImageViewTarget(view: SubsamplingScaleImageView)
    : CustomViewTarget<SubsamplingScaleImageView, File>(view) {
    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        view.setImage(ImageSource.uri(Uri.fromFile(resource)))
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        //Todo: Set failed image here
        view.setImage(ImageSource.resource(R.drawable.lazy_corgi))
    }

    override fun onResourceCleared(placeholder: Drawable?) {}
}