package com.vianh.blogtruyen.features.feed.list

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.MangaGridItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.list.MangaItem

class HistoryVH(
    parent: ViewGroup,
    itemClick: NewFeedAdapter.Callbacks
) : AbstractBindingHolder<MangaItem, Unit, MangaGridItemBinding>(R.layout.manga_grid_item, parent) {

    init {
        binding.root.updateLayoutParams {
            width = itemView.context.resources.getDimensionPixelSize(R.dimen.feed_history_width)
        }

        itemView.setOnClickListener {
            itemClick.onClick(it , boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: MangaItem, extra: Unit) {
        val manga = data.manga
        with(binding) {
            root.loadImage(manga.imageUrl)
            mangaName.text = manga.title
        }
    }

    override fun bindToView(view: View): MangaGridItemBinding {
        return MangaGridItemBinding.bind(view)
    }
}