package com.vianh.blogtruyen.features.history

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vianh.blogtruyen.databinding.HistoryItemBinding
import com.vianh.blogtruyen.databinding.TimeItemBinding
import com.vianh.blogtruyen.features.base.AbstractAdapter
import com.vianh.blogtruyen.features.base.AbstractViewHolder
import com.vianh.blogtruyen.utils.loadNetWorkImage

class HistoryAdapter(private val viewModel: HistoryViewModel) :
    AbstractAdapter<HistoryListItem, Unit>(Unit) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder<out HistoryListItem, Unit> {

        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HistoryListItem.TIME_ITEM -> HistoryTimeViewHolder.create(inflater, parent)
            HistoryListItem.HISTORY_ITEM -> HistoryItemVH.create(inflater, parent, viewModel)
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }
}

class HistoryItemVH(private val binding: HistoryItemBinding, private val viewModel: HistoryViewModel) :
    AbstractViewHolder<HistoryListItem.HistoryItem, Unit>(binding.root) {
    override fun onBind(data: HistoryListItem.HistoryItem, extra: Unit) {
        with(binding) {
            val history = data.history
            cover.loadNetWorkImage(history.manga.imageUrl)
            timeText.text = data.timeString
            mangaTitle.text = history.manga.title
            chapterName.text = history.chapter.name
        }
    }

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup, viewModel: HistoryViewModel): HistoryItemVH {
            return HistoryItemVH(HistoryItemBinding.inflate(inflater, parent, false), viewModel)
        }
    }
}

class HistoryTimeViewHolder(private val binding: TimeItemBinding): AbstractViewHolder<HistoryListItem.TimeItem, Unit>(binding.root) {

    override fun onBind(data: HistoryListItem.TimeItem, extra: Unit) {
        binding.time.text = data.time
    }

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup): HistoryTimeViewHolder {
            return HistoryTimeViewHolder(TimeItemBinding.inflate(inflater, parent, false))
        }
    }
}
