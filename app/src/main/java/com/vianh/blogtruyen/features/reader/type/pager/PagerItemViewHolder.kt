package com.vianh.blogtruyen.features.reader.type.pager

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.PagerReaderItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.reader.list.PageLoadCallBack
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import com.vianh.blogtruyen.utils.*
import timber.log.Timber
import java.io.File

class PagerItemViewHolder(parent: ViewGroup, val requestManager: RequestManager) :
    AbstractBindingHolder<ReaderItem.PageItem, Unit, PagerReaderItemBinding>(
        R.layout.pager_reader_item,
        parent
    ), View.OnClickListener, PageLoadCallBack<File> {

    private val imgTarget = SubsamplingScaleImageViewTarget(binding.page, this)

    private val scaleImageListener =
        object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
            override fun onImageLoaded() {
                binding.progressCircular.hide()
                binding.page.visible()
            }

            override fun onImageLoadError(e: Exception?) {
                super.onImageLoadError(e)
                showError(e)
            }
        }

    init {
        with(binding.page) {
            setMaxTileSize(getMaxTextureSize())
            setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
        }
    }

    override fun onBind(data: ReaderItem.PageItem, extra: Unit) {
        binding.progressCircular.show()
        binding.errText.gone()
        binding.retryButton.gone()
        binding.page.setOnImageEventListener(scaleImageListener)

        if (URLUtil.isNetworkUrl(data.uri)) {
            requestManager
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
        requestManager.clear(imgTarget)
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
            retryButton.setOnClickListener(this@PagerItemViewHolder)
        }
    }

    override fun bindToView(view: View): PagerReaderItemBinding {
        return PagerReaderItemBinding.bind(view)
    }
}