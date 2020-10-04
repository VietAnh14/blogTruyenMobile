package com.vianh.blogtruyen.ui.mangaViewer

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.Target
import com.vianh.blogtruyen.utils.GlideApp

class MangaViewerAdapter(private val viewModel: MangaViewerViewModel, var activity: MangaViewerActivity?):
    ListAdapter<String, PageViewHolder>(PageDiffCallBack()), ListPreloader.PreloadModelProvider<String> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        return PageViewHolder.from(parent, activity?.bitmapSize!!)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onViewRecycled(holder: PageViewHolder) {
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

    override fun getPreloadItems(position: Int): MutableList<String> = currentList.subList(position, position + 1)

    override fun getPreloadRequestBuilder(item: String): RequestBuilder<*>? {
        return GlideApp.with(activity!!).asFile().load(item).override(Target.SIZE_ORIGINAL)
    }
}