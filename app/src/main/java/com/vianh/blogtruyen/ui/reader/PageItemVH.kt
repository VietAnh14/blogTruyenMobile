package com.vianh.blogtruyen.ui.reader

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.ui.list.BaseVH
import com.vianh.blogtruyen.ui.list.ListItem
import com.vianh.blogtruyen.ui.mangaViewer.PageLoadCallBack
import com.vianh.blogtruyen.utils.SubsamplingScaleImageViewTarget
import timber.log.Timber
import java.io.File

class PageItemVH(val requestManager: RequestManager, binding: MangaPageItemBinding, tileSize: Int) :
    BaseVH<MangaPageItemBinding>(binding), PageLoadCallBack<File>, SubsamplingScaleImageView.OnImageEventListener {

    var data: PageItem? = null
//    val minHeight = itemView.context.resources.getDimensionPixelSize(R.dimen.app_margin)
    lateinit var imgPage: SubsamplingScaleImageView
    lateinit var progressBar: CircularProgressIndicator

    init {
        binding.page.setMaxTileSize(tileSize)
        binding.page.setMinimumDpi(70)
        Timber.d("Init page VH")
    }

    private val target = SubsamplingScaleImageViewTarget(binding.page, this)


    override fun onBind(item: ListItem) {
        binding.root.updateLayoutParams {
            height = 800
        }
        binding.progressCircular.show()
        binding.page.setOnImageEventListener(this)
        val page = item as PageItem
        data = page
        requestManager
            .download(page.uri)
            .into(target)
    }

    override fun onRecycle() {
        binding.page.recycle()
    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        target.view.setImage(ImageSource.uri(Uri.fromFile(resource)))
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        Timber.e("Failed to load ${data?.uri}")
    }

    override fun onLoadCleared(placeholder: Drawable?) {

    }

    override fun onReady() {
        binding.progressCircular.hide()
    }

    override fun onImageLoaded() {
        binding.root.updateLayoutParams {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        itemView.requestLayout()
    }

    override fun onPreviewLoadError(e: Exception?) {

    }

    override fun onImageLoadError(e: Exception?) {

    }

    override fun onTileLoadError(e: Exception?) {

    }

    override fun onPreviewReleased() {

    }

    companion object {
        fun initViews(context: Context) {
            val root = FrameLayout(context)
            root.updateLayoutParams {
                height = ViewGroup.LayoutParams.MATCH_PARENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            val imgPage = SubsamplingScaleImageView(context).apply {

            }
        }
    }
}