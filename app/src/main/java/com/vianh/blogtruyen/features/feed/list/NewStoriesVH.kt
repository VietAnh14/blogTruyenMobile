package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.NewUploadItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.feed.NewFeedItem
import com.vianh.blogtruyen.utils.loadNetWorkImage

class NewStoriesVH(parent: ViewGroup, itemClick: NewFeedAdapter.ItemClick) :
    AbstractBindingHolder<NewFeedItem.MangaItem, Unit, NewUploadItemBinding>(R.layout.new_upload_item, parent, { NewUploadItemBinding.bind(it)}) {

    init {
        itemView.setOnClickListener {
            itemClick.onItemClick(boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: NewFeedItem.MangaItem, extra: Unit) {
        val manga = data.item
        with(binding) {
            cover.loadNetWorkImage(manga.imageUrl)
            title.text = manga.title
        }
    }
}