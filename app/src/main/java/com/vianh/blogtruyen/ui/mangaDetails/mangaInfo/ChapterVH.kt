package com.vianh.blogtruyen.ui.mangaDetails.mangaInfo

import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.databinding.ChapterItemBinding

class ChapterVH(val binding: ChapterItemBinding, clickListener: ChapterClick): RecyclerView.ViewHolder(binding.root) {

    var data: Chapter? = null

    init {
        binding.chapterName.setOnClickListener {
            data?.let { clickListener.onChapterClick(it) }
        }
    }

    fun onBind(item: Chapter) {
        data = item
        binding.chapterName.text = item.name
    }

    interface ChapterClick {
        fun onChapterClick(chapter: Chapter)
    }
}