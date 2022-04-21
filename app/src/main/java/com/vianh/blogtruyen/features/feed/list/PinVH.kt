package com.vianh.blogtruyen.features.feed.list

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.PinnedItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.utils.ext.loadNetWorkImage
import jp.wasabeef.glide.transformations.BlurTransformation

class PinVH(parent: ViewGroup, itemClick: NewFeedAdapter.Callbacks) :
    AbstractBindingHolder<MangaItem, Unit, PinnedItemBinding>(R.layout.pinned_item, parent) {

    init {
        itemView.setOnClickListener {
            itemClick.onClick(it, boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: MangaItem, extra: Unit) {
        val manga = data.manga
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

    override fun bindToView(view: View): PinnedItemBinding {
        return PinnedItemBinding.bind(view)
    }
}