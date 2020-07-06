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
import java.io.File

class PageViewHolder(val binding: PageItemBinding) :
    RecyclerView.ViewHolder(binding.root), PageLoadCallBack<File> {
    val target = SubsamplingScaleImageViewTarget(binding.mangaPage, this)
    val bitmapTarget = BitmapTarget(binding.mangaPage)
    var status = NOT_LOAD
    var time = 0L

    fun onBind(url: String) {
        status = LOADING
        loadPage(url)
//        loadFromMemory(url)
    }

    fun recycle() {
        binding.mangaPage.recycle()
    }

    fun loadFromMemory(url: String) {
        GlideApp.with(binding.root.context).asBitmap()
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).load(url).into(bitmapTarget)
    }

    fun loadPage(url: String) {
        time = System.currentTimeMillis()
        Log.d("PAGE VIEW HOLDER", "START LOAD: $time")
        binding.holderImage.visibility = View.VISIBLE
        GlideApp.with(binding.root.context).download(url).skipMemoryCache(true)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(target)

    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        status = LOAD_SUCCESS
        resourceReady(resource)
    }

    private fun resourceReady(resource: File) {
        binding.mangaPage.setImage(ImageSource.uri(Uri.fromFile(resource)))
        binding.holderImage.visibility = View.GONE
        Log.d("ON BIND DONE", adapterPosition.toString())
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        status = LOAD_FAILED
        binding.holderImage.visibility = View.VISIBLE
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

        fun from(parent: ViewGroup): PageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PageItemBinding.inflate(inflater, parent, false)
            binding.mangaPage.apply {
                setOnImageEventListener(object :
                    SubsamplingScaleImageView.DefaultOnImageEventListener() {
                    override fun onReady() {
                        Log.d("PAGE VIEW HOLDER", "DONE LOAD: ${System.currentTimeMillis()}")
                    }
                })
            }
            return PageViewHolder(binding)
        }
    }


    inner class BitmapTarget(view: SubsamplingScaleImageView)
        : CustomViewTarget<SubsamplingScaleImageView, Bitmap>(view) {
        override fun onLoadFailed(errorDrawable: Drawable?) {
            Log.d("BITMAP TARGET", "LOAD FAILED")
        }

        override fun onResourceCleared(placeholder: Drawable?) {
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            getView().setImage(ImageSource.cachedBitmap(resource))
            binding.holderImage.visibility = View.GONE
            Log.d("BITMAP TARGET", "DONE GET RESOURCE")
        }
    }
}