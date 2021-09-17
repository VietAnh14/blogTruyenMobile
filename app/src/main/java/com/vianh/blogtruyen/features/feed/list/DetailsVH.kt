package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.DetailsMangaListItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.utils.loadNetWorkImage

class DetailsVH(parent: ViewGroup, itemClick: NewFeedAdapter.ItemClick):
    AbstractBindingHolder<NewFeedItem.MangaItem, Unit, DetailsMangaListItemBinding>(R.layout.details_manga_list_item, parent, { DetailsMangaListItemBinding.bind(it) }) {

    init {
        itemView.setOnClickListener {
            itemClick.onItemClick(boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: NewFeedItem.MangaItem, extra: Unit) {
        with(binding) {
            val manga = data.item
            cover.loadNetWorkImage(manga.imageUrl)
            title.text = manga.title
            summary.text = manga.description
        }
    }
}