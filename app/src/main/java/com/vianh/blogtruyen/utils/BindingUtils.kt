package com.vianh.blogtruyen.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.vianh.blogtruyen.BuildConfig
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.main.DashBoardAdapter

@BindingAdapter("items")
fun setItems(recycler: RecyclerView, items: List<Manga>) {
    val adapter = recycler.adapter as DashBoardAdapter
    adapter.setItems(items)
}

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    url?.let {
        val glideUrl = GlideUrl(
            url, LazyHeaders.Builder()
                .addHeader("Referer", BuildConfig.HOST)
                .build()
        )
        Glide.with(imageView.context)
            .load(glideUrl)
            .error(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
            .into(imageView)
    }
}