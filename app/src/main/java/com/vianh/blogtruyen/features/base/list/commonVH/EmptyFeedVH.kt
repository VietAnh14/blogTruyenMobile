package com.vianh.blogtruyen.features.base.list.commonVH

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.EmptyMangaItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.base.list.items.EmptyItem

class EmptyFeedVH(parent: ViewGroup) :
    AbstractBindingHolder<EmptyItem, Unit, EmptyMangaItemBinding>(R.layout.empty_manga_item, parent, { EmptyMangaItemBinding.bind(it) }) {

    override fun onBind(data: EmptyItem, extra: Unit) {
        with(binding) {
            emptyText.text = data.message
            icon.setImageResource(data.icon)
        }
        return
    }
}