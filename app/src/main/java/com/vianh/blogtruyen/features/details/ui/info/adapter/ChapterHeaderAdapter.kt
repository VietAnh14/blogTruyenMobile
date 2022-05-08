package com.vianh.blogtruyen.features.details.ui.info.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.ChapterHeaderItemBinding
import com.vianh.blogtruyen.features.details.ui.MangaDetailsViewModel

class ChapterHeaderAdapter(private val viewModel: MangaDetailsViewModel) : ListAdapter<HeaderItem, HeaderVH>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderVH {
        val inflater = LayoutInflater.from(parent.context)
        return HeaderVH(ChapterHeaderItemBinding.inflate(inflater, parent, false), viewModel)
    }

    override fun onBindViewHolder(holder: HeaderVH, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: HeaderVH, position: Int) {
        holder.onBind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<HeaderItem>() {
        override fun areItemsTheSame(oldItem: HeaderItem, newItem: HeaderItem): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: HeaderItem, newItem: HeaderItem): Boolean {
            return newItem == oldItem
        }

        override fun getChangePayload(oldItem: HeaderItem, newItem: HeaderItem): Any? {
            return if (newItem.descendingSort != oldItem.descendingSort) newItem.descendingSort else null
        }
    }

}

class HeaderVH(private val binding: ChapterHeaderItemBinding, viewModel: MangaDetailsViewModel) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            viewModel.toggleSortType()
        }
    }

    fun onBind(headerItem: HeaderItem) {
        with(binding) {
            chapterNumber.text =
                itemView.context.getString(R.string.chapter_header_title, headerItem.chapterCount)

            val sortDrawable = if (headerItem.descendingSort) {
                ContextCompat.getDrawable(itemView.context, R.drawable.ic_arrow_circle_up)
            } else {
                ContextCompat.getDrawable(itemView.context, R.drawable.ic_arrow_circle_down)
            }
            btnToggleSort.setCompoundDrawablesWithIntrinsicBounds(null, null, sortDrawable, null)

        }
    }
}

data class HeaderItem(val chapterCount: Int, val descendingSort: Boolean = true)