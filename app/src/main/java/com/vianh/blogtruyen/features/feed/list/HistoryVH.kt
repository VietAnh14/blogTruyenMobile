package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.MangaGridItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.utils.loadNetWorkImage

class HistoryVH(
    parent: ViewGroup,
    itemClick: NewFeedAdapter.ItemClick
) :
    AbstractBindingHolder<NewFeedItem.MangaItem, Unit, MangaGridItemBinding>(R.layout.manga_grid_item, parent, { MangaGridItemBinding.bind(it)}) {

    init {
        binding.root.updateLayoutParams {
            width = itemView.context.resources.getDimensionPixelSize(R.dimen.feed_history_width)
        }

        itemView.setOnClickListener {
            itemClick.onItemClick(boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: NewFeedItem.MangaItem, extra: Unit) {
        val manga = data.item
        with(binding) {
            imageCover.loadNetWorkImage(manga.imageUrl)
            mangaName.text = manga.title
        }
    }
}