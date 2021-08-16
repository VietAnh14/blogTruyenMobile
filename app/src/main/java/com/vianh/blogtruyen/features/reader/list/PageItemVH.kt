package com.vianh.blogtruyen.features.reader.list

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.webkit.URLUtil
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.features.base.BaseViewHolder
import com.vianh.blogtruyen.features.base.list.ListItem
import com.vianh.blogtruyen.utils.SubsamplingScaleImageViewTarget
import com.vianh.blogtruyen.utils.gone
import com.vianh.blogtruyen.utils.invisible
import com.vianh.blogtruyen.utils.visible
import java.io.File

class PageItemVH(
    val binding: MangaPageItemBinding,
    private val glideRequestManager: RequestManager
) : BaseViewHolder(binding.root), PageLoadCallBack<File> {

    var data: ReaderItem.PageItem? = null
    private val imgTarget = SubsamplingScaleImageViewTarget(binding.page, this)

    private val scaleImageListener = object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
        override fun onImageLoaded() {
            binding.progressCircular.gone()
            binding.page.visible()
            binding.page.updateLayoutParams {
                height = WRAP_CONTENT
            }
            binding.root.updateLayoutParams {
                height = WRAP_CONTENT
            }
        }
    }

    override fun onBind(item: ListItem) {
        val boundItem = item as ReaderItem.PageItem
        data = boundItem
        binding.progressCircular.visible()
        binding.page.setOnImageEventListener(scaleImageListener)

        if (URLUtil.isNetworkUrl(boundItem.uri)) {
            glideRequestManager
                .download(boundItem.uri)
                .skipMemoryCache(true)
                .dontAnimate()
                .dontTransform()
                .into(imgTarget)
        } else {
            onResourceReady(File(boundItem.uri), null)
        }
    }

    override fun onRecycle() {
        with(binding) {
            page.recycle()
            page.invisible()
            root.updateLayoutParams {
                height = itemView.context.resources.getDimensionPixelSize(R.dimen.page_item_min_height)
            }
            glideRequestManager.clear(imgTarget)
        }
        super.onRecycle()
    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        binding.page.setImage(ImageSource.uri(Uri.fromFile(resource)))
    }

    override fun onLoadFailed(errorDrawable: Drawable?) = Unit

    override fun onLoadCleared(placeholder: Drawable?) = Unit
}