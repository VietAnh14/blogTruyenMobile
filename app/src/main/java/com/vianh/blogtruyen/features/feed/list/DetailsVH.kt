package com.vianh.blogtruyen.features.feed.list

import android.view.View
import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.DetailsMangaListItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.base.list.ItemClick
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.utils.ext.loadNetWorkImage

class DetailsVH(parent: ViewGroup, itemClick: ItemClick<MangaItem>):
    AbstractBindingHolder<MangaItem, Unit, DetailsMangaListItemBinding>(R.layout.details_manga_list_item, parent) {

    init {
        itemView.setOnClickListener {
            itemClick.onClick(it,boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: MangaItem, extra: Unit) {
        with(binding) {
            val manga = data.manga
            cover.loadNetWorkImage(manga.imageUrl)
            title.text = manga.title
            summary.text = manga.description
        }
    }

    override fun bindToView(view: View): DetailsMangaListItemBinding {
        return DetailsMangaListItemBinding.bind(view)
    }
}