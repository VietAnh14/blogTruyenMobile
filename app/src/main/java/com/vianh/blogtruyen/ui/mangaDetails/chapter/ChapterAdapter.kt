package com.vianh.blogtruyen.ui.mangaDetails.chapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.databinding.ChapterItemBinding

class ChapterAdapter(val chapterClick: ChapterVH.ChapterClick): ListAdapter<Chapter, ChapterVH>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterVH {
        val inflater = LayoutInflater.from(parent.context)
        return ChapterVH(ChapterItemBinding.inflate(inflater, parent, false), chapterClick)
    }

    override fun onBindViewHolder(holder: ChapterVH, position: Int) {
        holder.onBind(getItem(position))
    }

    class DiffCallback: DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem == newItem
        }

    }
}