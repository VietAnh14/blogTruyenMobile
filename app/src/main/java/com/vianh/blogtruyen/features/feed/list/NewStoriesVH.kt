package com.vianh.blogtruyen.features.feed.list

import android.view.View
import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.NewUploadItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.base.list.ItemClick
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.utils.loadNetWorkImage

class NewStoriesVH(parent: ViewGroup, itemClick: ItemClick<MangaItem>) :
    AbstractBindingHolder<MangaItem, Unit, NewUploadItemBinding>(R.layout.new_upload_item, parent) {

    init {
        itemView.setOnClickListener {
            itemClick.onClick(it, boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: MangaItem, extra: Unit) {
        val manga = data.manga
        with(binding) {
            cardContainer.loadImage(manga.imageUrl)
            title.text = manga.title
        }
    }

    override fun bindToView(view: View): NewUploadItemBinding {
        return NewUploadItemBinding.bind(view)
    }

    override fun onRecycle() {
        binding.cardContainer.clearLoading()
        super.onRecycle()
    }
}