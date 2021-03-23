package com.vianh.blogtruyen.ui.reader

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.ui.base.BaseViewHolder
import com.vianh.blogtruyen.ui.list.ListItem
import com.vianh.blogtruyen.ui.mangaViewer.PageLoadCallBack
import com.vianh.blogtruyen.utils.SubsamplingScaleImageViewTarget
import timber.log.Timber
import java.io.File
import kotlin.system.measureTimeMillis

class PageMannualCreateVH(
    val binding: MannualPageBinding,
    val glideRequestManager: RequestManager
) : BaseViewHolder(binding.root), PageLoadCallBack<File>,
    SubsamplingScaleImageView.OnImageEventListener {

    var loadTime = 0L
    var data: PageItem? = null
    val imgTarget = SubsamplingScaleImageViewTarget(binding.page, this)

    override fun onBind(item: ListItem) {
        val boundItem = item as PageItem
        binding.progress.show()
        binding.page.setOnImageEventListener(this)
        loadTime = System.currentTimeMillis()
        glideRequestManager
            .download(boundItem.uri)
            .into(imgTarget)
    }

    override fun onRecycle() {
        with(binding) {
            page.recycle()
            glideRequestManager.clear(imgTarget)
            progress.show()
        }
        super.onRecycle()
    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        val currentTime = System.currentTimeMillis()
        Timber.d("Load time $adapterPosition: ${currentTime - loadTime}")
        loadTime = currentTime
        binding.progress.hide()
        binding.page.setImage(ImageSource.uri(Uri.fromFile(resource)))
    }

    override fun onLoadFailed(errorDrawable: Drawable?) = Unit

    override fun onLoadCleared(placeholder: Drawable?) = Unit

    override fun onReady() = Unit

    override fun onImageLoaded() {
        binding.root.updateLayoutParams {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    override fun onPreviewLoadError(e: Exception?) = Unit

    override fun onImageLoadError(e: Exception?) = Unit

    override fun onTileLoadError(e: Exception?) = Unit

    override fun onPreviewReleased() = Unit

    companion object {
        fun createView(context: Context, tileSize: Int): MannualPageBinding {
            val root = FrameLayout(context)
            root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            root.minimumHeight =
                context.resources.getDimensionPixelSize(R.dimen.page_item_min_height)
            val page = SubsamplingScaleImageView(context)
            page.setMaxTileSize(tileSize)
            val progressIndicator = CircularProgressIndicator(context)
            progressIndicator.isIndeterminate = true
            progressIndicator.setIndicatorColor(android.R.attr.colorAccent)
            progressIndicator.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            root.addView(page)
            root.addView(progressIndicator)
            return MannualPageBinding(root, progressIndicator, page)
        }
    }
}

data class MannualPageBinding(
    val root: FrameLayout,
    val progress: CircularProgressIndicator,
    val page: SubsamplingScaleImageView
)