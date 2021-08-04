package com.vianh.blogtruyen.features.mangaDetails.mangaInfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vianh.blogtruyen.databinding.ChapterItemBinding
import com.vianh.blogtruyen.features.mangaDetails.mangaInfo.ChapterVH
import kotlinx.coroutines.CoroutineScope

class ChapterAdapter(
    private val chapterClick: ChapterVH.ChapterClick,
    private val scope: CoroutineScope
) : ListAdapter<ChapterItem, ChapterVH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterVH {
        val inflater = LayoutInflater.from(parent.context)
        return ChapterVH(ChapterItemBinding.inflate(inflater, parent, false), chapterClick, scope)
    }

    override fun onBindViewHolder(holder: ChapterVH, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onViewRecycled(holder: ChapterVH) {
        holder.onRecycle()
        super.onViewRecycled(holder)
    }

    override fun onViewDetachedFromWindow(holder: ChapterVH) {
        holder.onDetached()
        super.onViewDetachedFromWindow(holder)
    }

    class DiffCallback : DiffUtil.ItemCallback<ChapterItem>() {
        override fun areItemsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean {
            return oldItem.chapter.id == newItem.chapter.id
        }

        override fun areContentsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean {
            return oldItem.chapter == newItem.chapter && oldItem.downloadState.value == newItem.downloadState.value
        }

    }
}