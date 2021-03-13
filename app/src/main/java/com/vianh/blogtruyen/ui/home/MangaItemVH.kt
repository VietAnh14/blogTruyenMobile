package com.vianh.blogtruyen.ui.home

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.vianh.blogtruyen.databinding.FeedItemBinding
import com.vianh.blogtruyen.ui.list.BaseVM
import com.vianh.blogtruyen.ui.list.ListItem

class MangaItemVH(binding: FeedItemBinding, clickListener: MangaClick) :
    BaseVM<FeedItemBinding>(binding) {

    var data: ListItem? = null

    init {
        itemView.setOnClickListener {
            data?.let {
                clickListener.onMangaItemClick(it as MangaItem)
            }
        }
    }

    override fun onBind(item: ListItem) {
        val mangaItem = item as MangaItem
        data = mangaItem
        with(binding) {
            Glide.with(itemView.context)
                .load(mangaItem.manga.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageCover)

            mangaName.text = mangaItem.manga.title
        }
    }

    override fun onRecycle() {
        super.onRecycle()
    }

    public interface MangaClick {
        fun onMangaItemClick(mangaItem: MangaItem)
    }
}