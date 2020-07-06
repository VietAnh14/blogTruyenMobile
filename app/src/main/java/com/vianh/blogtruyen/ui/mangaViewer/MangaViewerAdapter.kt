package com.vianh.blogtruyen.ui.mangaViewer

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class MangaViewerAdapter(private val viewModel: MangaViewerViewModel, var activity: MangaViewerActivity?):
    ListAdapter<String, PageViewHolder>(PageDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        return PageViewHolder.from(parent, activity?.bitmapSize!!)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onViewRecycled(holder: PageViewHolder) {
        Log.d("ON RECYCLED ", holder.adapterPosition.toString())
        holder.recycle()
        super.onViewRecycled(holder)
    }

    class PageDiffCallBack: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    fun onDestroy() {
        activity = null
    }
}