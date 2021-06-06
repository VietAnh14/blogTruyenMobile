package com.vianh.blogtruyen.features.home.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.vianh.blogtruyen.databinding.FeedItemBinding
import com.vianh.blogtruyen.features.base.AbstractViewHolder
import com.vianh.blogtruyen.features.list.BaseVH
import com.vianh.blogtruyen.features.list.ListItem
import com.vianh.blogtruyen.utils.loadNetWorkImage

class MangaItemVH(private val binding: FeedItemBinding, clickListener: MangaClick) :
    AbstractViewHolder<MangaItem, Unit>(binding.root) {

    init {
        itemView.setOnClickListener {
            boundData?.let {
                clickListener.onMangaItemClick(it)
            }
        }
    }

    override fun onBind(data: MangaItem, extra: Unit) {
        with(binding) {
            imageCover.loadNetWorkImage(data.manga.imageUrl)
            mangaName.text = data.manga.title
        }
    }

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup, listener: MangaClick): MangaItemVH {
            return MangaItemVH(FeedItemBinding.inflate(inflater, parent, false), listener)
        }
    }

    interface MangaClick {
        fun onMangaItemClick(mangaItem: MangaItem)
    }
}