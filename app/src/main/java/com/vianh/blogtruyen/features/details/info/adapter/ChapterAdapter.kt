package com.vianh.blogtruyen.features.details.info.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.databinding.ChapterItemBinding
import kotlinx.coroutines.CoroutineScope

class ChapterAdapter(
    private val chapterClick: ChapterVH.ChapterClick,
    private val scope: CoroutineScope
) : ListAdapter<ChapterItem, ChapterVH>(DiffCallback()) {

    val selectedChapters = mutableSetOf<Chapter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterVH {
        val inflater = LayoutInflater.from(parent.context)
        return ChapterVH(ChapterItemBinding.inflate(inflater, parent, false), chapterClick, scope)
    }

    override fun onBindViewHolder(holder: ChapterVH, position: Int) {
        holder.onBind(getItem(position), selectedChapters)
    }

    override fun onViewRecycled(holder: ChapterVH) {
        holder.onRecycle()
        super.onViewRecycled(holder)
    }

    override fun onViewDetachedFromWindow(holder: ChapterVH) {
        holder.onDetached()
        super.onViewDetachedFromWindow(holder)
    }

    fun selectChapter(chapter: ChapterItem) {
        if (selectedChapters.contains(chapter.chapter)) {
            selectedChapters.remove(chapter.chapter)
        } else {
            selectedChapters.add(chapter.chapter)
        }
        notifyItemChanged(currentList.indexOf(chapter))
    }

    fun selectChapters(chapters: List<Chapter>?) {
        if (chapters.isNullOrEmpty())
            return

        selectedChapters.addAll(chapters)
        notifyDataSetChanged()
    }

    fun hasSelectedChapters(): Boolean {
        return selectedChapters.isNotEmpty()
    }

    fun clearSelections() {
        selectedChapters.clear()
        notifyDataSetChanged()
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