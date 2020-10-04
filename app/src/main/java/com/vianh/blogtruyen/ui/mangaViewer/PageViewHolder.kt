package com.vianh.blogtruyen.ui.mangaViewer

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.databinding.PageItemBinding
import com.vianh.blogtruyen.utils.GlideApp
import com.vianh.blogtruyen.utils.SubsamplingScaleImageViewTarget
import com.vianh.blogtruyen.utils.hide
import com.vianh.blogtruyen.utils.show
import java.io.File

class PageViewHolder(val binding: PageItemBinding) :
    RecyclerView.ViewHolder(binding.root), PageLoadCallBack<File> {
    private val target = SubsamplingScaleImageViewTarget(binding.mangaPage, this)
    private val bitmapTarget = BitmapTarget(binding.mangaPage)
    var status = NOT_LOAD
    private val imageListener = object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
        override fun onImageLoaded() {
            binding.progressCircular.hide()
            binding.mangaPage.show()
        }

        override fun onImageLoadError(e: Exception?) {
//            binding.holderImage.show()
        }
    }

    fun onBind(url: String) {
        status = LOADING
        loadPage(url)
    }

    fun recycle() {
        binding.mangaPage.recycle()
    }

    fun loadFromMemory(url: String) {
        GlideApp.with(binding.root.context).asBitmap()
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).load(url).into(bitmapTarget)
    }

    fun loadPage(url: String) {
        binding.progressCircular.show()
        val extension = url.split(".").last()
        if (extension == "gif") {
            binding.mangaPage.hide()
//            GlideApp.with(binding.root.context).asGif().fitCenter().load(url).into(binding.holderImage)
        } else {
            binding.mangaPage.setOnImageEventListener(imageListener)
            GlideApp
                .with(binding.root.context)
                .asFile()
                .load(url)
                .into(target)
        }
    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        Log.d("ADAPTER","DONE LOAD RESOURCE $adapterPosition")
        status = LOAD_SUCCESS
        resourceReady(resource)
    }

    private fun resourceReady(resource: File) {
        binding.mangaPage.setImage(ImageSource.uri(Uri.fromFile(resource)))
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        status = LOAD_FAILED
        Log.d("LOAD FAILED", adapterPosition.toString())
        // Todo: set a failed image here
    }

    override fun onLoadCleared(placeholder: Drawable?) {
    }

    companion object PageStatus {
        const val NOT_LOAD = 0
        const val LOADING = 1
        const val LOAD_SUCCESS = 2
        const val LOAD_FAILED = 3

        fun from(parent: ViewGroup, bitmapSize: Int): PageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PageItemBinding.inflate(inflater, parent, false)
            binding.mangaPage.apply {
                setMaxTileSize(bitmapSize)
            }
            return PageViewHolder(binding)
        }
    }


    class BitmapTarget(view: SubsamplingScaleImageView) :
        CustomViewTarget<SubsamplingScaleImageView, Bitmap>(view) {
        override fun onLoadFailed(errorDrawable: Drawable?) {
            Log.d("BITMAP TARGET", "LOAD FAILED")
        }

        override fun onResourceCleared(placeholder: Drawable?) {
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            getView().setImage(ImageSource.cachedBitmap(resource))
        }
    }
}