package com.vianh.blogtruyen.features.mangaDetails.mangaInfo

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.databinding.ChapterItemBinding
import com.vianh.blogtruyen.utils.getColorFromAttr

class ChapterVH(val binding: ChapterItemBinding, clickListener: ChapterClick): RecyclerView.ViewHolder(binding.root) {

    var data: Chapter? = null

    init {
        binding.chapterName.setOnClickListener {
            data?.let { clickListener.onChapterClick(it) }
        }
    }

    @SuppressLint("ResourceType")
    fun onBind(item: Chapter) {
        data = item
        binding.chapterName.text = item.name
        val textColor = if (item.read) {
            itemView.context.getColorFromAttr(android.R.attr.colorControlNormal)
        } else {
            itemView.context.getColorFromAttr(android.R.attr.textColorPrimary)
        }
        binding.chapterName.setTextColor(textColor)
    }

    interface ChapterClick {
        fun onChapterClick(chapter: Chapter)
    }
}