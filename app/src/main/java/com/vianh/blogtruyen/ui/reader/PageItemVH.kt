package com.vianh.blogtruyen.ui.reader

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.ui.mangaViewer.PageLoadCallBack
import com.vianh.blogtruyen.utils.SubsamplingScaleImageViewTarget
import com.vianh.blogtruyen.utils.gone
import timber.log.Timber
import java.io.File

class PageItemVH(val requestManager: RequestManager, val binding: MangaPageItemBinding, tileSize: Int) :
    RecyclerView.ViewHolder(binding.root), PageLoadCallBack<File> {

    var uri: String? = null

    init {
        binding.page.setMaxTileSize(tileSize)
        binding.page.setMinimumDpi(70)
        Timber.d("Init page VH")
    }

    private val target = SubsamplingScaleImageViewTarget(binding.page, this)
    fun onBind(uri: String) {
        requestManager
            .download(uri)
            .into(target)
    }

    fun onRecycle() {
        binding.page.recycle()
    }

    fun resetLayout() {
        binding.root.updateLayoutParams {
            this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        binding.progressCircular.gone()
        target.view.setImage(ImageSource.uri(Uri.fromFile(resource)).tilingDisabled())
        resetLayout()
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        Timber.e("Failed to load $uri")
    }

    override fun onLoadCleared(placeholder: Drawable?) {

    }
}