package com.vianh.blogtruyen.features.home.list

import com.vianh.blogtruyen.databinding.FeedItemBinding
import com.vianh.blogtruyen.features.list.BaseVH
import com.vianh.blogtruyen.features.list.ListItem
import com.vianh.blogtruyen.utils.loadNetWorkImage

class MangaItemVH(binding: FeedItemBinding, clickListener: MangaClick) :
    BaseVH<FeedItemBinding>(binding) {

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
            imageCover.loadNetWorkImage(mangaItem.manga.imageUrl)

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