package com.vianh.blogtruyen.ui.mangaInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.databinding.ChapterItemBinding
import com.vianh.blogtruyen.ui.base.BaseBindingViewHolder

class ChapterAdapter(private val viewModel: MangaInfoViewModel):
    RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {
    var items: List<Chapter> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    inner class ChapterViewHolder(binding: ChapterItemBinding):
        BaseBindingViewHolder<ChapterItemBinding>(binding) {
        override fun onBind(position: Int) {
            getBinding().viewModel = viewModel
            getBinding().chapter = items[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ChapterItemBinding.inflate(layoutInflater, parent, false)
        return ChapterViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.onBind(position)
    }
}