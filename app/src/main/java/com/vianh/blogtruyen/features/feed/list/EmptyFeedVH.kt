package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.EmptyMangaItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.feed.NewFeedItem

class EmptyFeedVH(parent: ViewGroup) :
    AbstractBindingHolder<NewFeedItem.EmptyItem, Unit, EmptyMangaItemBinding>(R.layout.empty_manga_item, parent, { EmptyMangaItemBinding.bind(it) }) {

    override fun onBind(data: NewFeedItem.EmptyItem, extra: Unit) {
        with(binding) {
            emptyText.text = data.message
            icon.setImageResource(data.icon)
        }
        return
    }
}