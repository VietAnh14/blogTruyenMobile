package com.vianh.blogtruyen.features.reader.type.vertical

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.webkit.URLUtil
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.reader.list.PageLoadCallBack
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import com.vianh.blogtruyen.utils.SubsamplingScaleImageViewTarget
import com.vianh.blogtruyen.utils.gone
import com.vianh.blogtruyen.utils.invisible
import com.vianh.blogtruyen.utils.visible
import timber.log.Timber
import java.io.File

class PageItemVH(
    parent: ViewGroup,
    private val glideRequestManager: RequestManager,
    tileSize: Int
) : AbstractBindingHolder<ReaderItem.PageItem, Unit, MangaPageItemBinding>(R.layout.manga_page_item, parent), PageLoadCallBack<File>, View.OnClickListener {

    private val minHeight = itemView.context.resources.getDimensionPixelSize(R.dimen.page_item_min_height)
    private val imgTarget = SubsamplingScaleImageViewTarget(binding.page, this)

    private val scaleImageListener =
        object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
            override fun onImageLoaded() {
                binding.progressCircular.hide()
                binding.page.visible()
                binding.root.updateLayoutParams {
                    height = WRAP_CONTENT
                }
            }

            override fun onImageLoadError(e: Exception?) {
                super.onImageLoadError(e)
                showError(e)
            }
        }

    init {
        with(binding.page) {
            setMaxTileSize(tileSize)
            setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
            setMinimumDpi(90)
            setMinimumTileDpi(180)
        }
    }

    override fun onBind(data: ReaderItem.PageItem, extra: Unit) {
        binding.progressCircular.show()
        binding.errText.gone()
        binding.retryButton.gone()
        binding.page.setOnImageEventListener(scaleImageListener)

        if (URLUtil.isNetworkUrl(data.uri)) {
            glideRequestManager
                .download(data.uri)
                .skipMemoryCache(true)
                .dontAnimate()
                .dontTransform()
                .into(imgTarget)
        } else {
            onResourceReady(File(data.uri), null)
        }
    }

    override fun onRecycle() {
        binding.page.recycle()
        binding.page.invisible()
        binding.root.updateLayoutParams { height = minHeight }
        glideRequestManager.clear(imgTarget)
        super.onRecycle()
    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        binding.page.setImage(ImageSource.uri(Uri.fromFile(resource)))
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        showError()
    }

    override fun onLoadCleared(placeholder: Drawable?) = Unit


    override fun onClick(v: View?) {
        onBind(boundData ?: return, Unit)
    }

    private fun showError(err: Exception? = null) {
        Timber.e(err)
        val message = err?.message ?: "Failed to load image from uri"
        with(binding) {
            page.gone()
            progressCircular.gone()
            errText.visible()
            errText.text = message
            retryButton.visible()
            retryButton.setOnClickListener(this@PageItemVH)
        }
    }

    override fun bindToView(view: View): MangaPageItemBinding {
        return MangaPageItemBinding.bind(view)
    }
}