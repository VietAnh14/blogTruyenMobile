package com.vianh.blogtruyen.features.details.ui.info.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.details.ui.info.MangaInfoHeaderVH

class InfoHeaderAdapter(): ListAdapter<Manga, MangaInfoHeaderVH>(
    DiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaInfoHeaderVH {
        return MangaInfoHeaderVH.newInstance(parent)
    }

    override fun onBindViewHolder(holder: MangaInfoHeaderVH, position: Int) {
        holder.onBind(getItem(position))
    }

    fun submitItem(manga: Manga) {
        submitList(mutableListOf(manga))
    }

    class DiffCallBack: DiffUtil.ItemCallback<Manga>() {
        override fun areItemsTheSame(oldItem: Manga, newItem: Manga): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Manga, newItem: Manga): Boolean {
            return oldItem == newItem
        }

    }
}