package com.vianh.blogtruyen.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
        GlideApp.with(imageView.context)
            .load(url)
            .error(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
            .into(imageView)
    }
}

@BindingAdapter("onRefresh")
fun bindSwipeListener(view: SwipeRefreshLayout, listener: SwipeRefreshLayout.OnRefreshListener) {
    view.setOnRefreshListener(listener)
}

//@BindingAdapter("mangaPageUrl")
//fun loadImage(view: SubsamplingScaleImageView, url: String) {
//    GlideApp.with(view.context).download(url).into(SubsamplingScaleImageViewTarget(view))
//}