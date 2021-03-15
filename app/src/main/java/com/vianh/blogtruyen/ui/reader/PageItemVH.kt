package com.vianh.blogtruyen.ui.reader

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.ui.mangaViewer.PageLoadCallBack
import com.vianh.blogtruyen.utils.SubsamplingScaleImageViewTarget
import timber.log.Timber
import java.io.File

class PageItemVH(val requestManager: RequestManager, val binding: MangaPageItemBinding) :
    RecyclerView.ViewHolder(binding.root), PageLoadCallBack<File> {

    var uri: String? = null

    private val target = SubsamplingScaleImageViewTarget(binding.page, this)
    fun onBind(uri: String) {
        requestManager
            .download(uri)
            .into(target)
    }

    fun onRecycle() {
        binding.page.recycle()
    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        target.view.setImage(ImageSource.uri(Uri.fromFile(resource)))
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        Timber.e("Failed to load $uri")
    }

    override fun onLoadCleared(placeholder: Drawable?) {

    }
}