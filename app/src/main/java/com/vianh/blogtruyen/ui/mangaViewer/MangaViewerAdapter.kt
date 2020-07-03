package com.vianh.blogtruyen.ui.mangaViewer

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.databinding.PageItemBinding

class MangaViewerAdapter(private val viewModel: MangaViewerViewModel):
    ListAdapter<String, MangaViewerAdapter.PageViewHolder>(PageDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        return PageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        Log.d("ON BIND ", position.toString())
        holder.onBind(getItem(position))
    }


    class PageViewHolder(val binding: PageItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(pageLink: String) {
            binding.link = pageLink
        }

        companion object {
            fun from(parent: ViewGroup): PageViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = PageItemBinding.inflate(inflater, parent, false)
                return PageViewHolder(binding)
            }
        }
    }

    override fun onViewRecycled(holder: PageViewHolder) {
        holder.binding.mangaPage.recycle()
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
}