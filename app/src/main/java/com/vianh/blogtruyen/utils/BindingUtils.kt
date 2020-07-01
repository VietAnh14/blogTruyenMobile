package com.vianh.blogtruyen.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.Target
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.BuildConfig
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.ui.mangaViewer.MangaViewerAdapter

@BindingAdapter("items")
fun setItems(recycler: RecyclerView, items: List<String>?) {
    items?.let {
        val adapter = recycler.adapter as MangaViewerAdapter
        adapter.submitList(it)
    }
}

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    url?.let {
        val glideUrl = GlideUrl(
            url, LazyHeaders.Builder()
                .addHeader("Referer", BuildConfig.HOST)
                .build()
        )
        GlideApp.with(imageView.context)
            .load(glideUrl)
            .error(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
            .into(imageView)
    }
}

@BindingAdapter("mangaPageUrl")
fun loadImage(view: SubsamplingScaleImageView, url: String) {
    val glideUrl = GlideUrl(
        url, LazyHeaders.Builder()
            .addHeader("Referer", BuildConfig.HOST)
            .build()
    )
    Glide.with(view.context).download(glideUrl).into(SubsamplingScaleImageViewTarget(view))
}