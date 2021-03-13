package com.vianh.blogtruyen.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vianh.blogtruyen.databinding.FeedItemBinding
import com.vianh.blogtruyen.ui.list.BaseVH
import com.vianh.blogtruyen.ui.list.ListItem
import java.lang.IllegalArgumentException

class MangaFeedAdapter(val onItemClick: MangaItemVH.MangaClick): ListAdapter<ListItem, BaseVH<*>>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*> {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            ListItem.MANGA_ITEM -> MangaItemVH(FeedItemBinding.inflate(inflater, parent, false), onItemClick)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseVH<*>, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    class DiffCallback: DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is MangaItem && newItem is MangaItem -> oldItem.manga.id == newItem.manga.id
                else -> oldItem.javaClass == newItem.javaClass
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.equals(newItem)
        }

    }
}