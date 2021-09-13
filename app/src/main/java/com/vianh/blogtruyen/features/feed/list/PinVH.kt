package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.PinnedItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.feed.NewFeedItem
import com.vianh.blogtruyen.utils.loadNetWorkImage
import jp.wasabeef.glide.transformations.BlurTransformation

class PinVH(parent: ViewGroup, itemClick: NewFeedAdapter.ItemClick) :
    AbstractBindingHolder<NewFeedItem.MangaItem, Unit, PinnedItemBinding>(R.layout.pinned_item, parent, { PinnedItemBinding.bind(it) }) {

    init {
        itemView.setOnClickListener {
            itemClick.onItemClick(boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: NewFeedItem.MangaItem, extra: Unit) {
        val manga = data.item
        with(binding) {
            title.text = manga.title
            summary.text = manga.description
            smallCover.loadNetWorkImage(manga.imageUrl)
            Glide.with(itemView.context)
                .load(manga.imageUrl)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 3)))
                .into(bgBlur)
        }
    }
}