package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.MangaGridItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.utils.loadNetWorkImage

class UpdateVH(parent: ViewGroup, itemClick: NewFeedAdapter.Callbacks) :
    AbstractBindingHolder<MangaItem, Unit, MangaGridItemBinding>(R.layout.manga_grid_item, parent, { MangaGridItemBinding.bind(it) }) {

    init {
        itemView.setOnClickListener {
            itemClick.onClick(it, boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: MangaItem, extra: Unit) {
        val manga = data.manga
        with(binding) {
            imageCover.loadNetWorkImage(manga.imageUrl)
            mangaName.text = manga.title
        }
    }
}