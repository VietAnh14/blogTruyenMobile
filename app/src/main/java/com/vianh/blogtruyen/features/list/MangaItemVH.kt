package com.vianh.blogtruyen.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.databinding.FeedItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.ItemClick
import com.vianh.blogtruyen.utils.gone
import com.vianh.blogtruyen.utils.loadNetWorkImage
import com.vianh.blogtruyen.utils.visible

class MangaItemVH(private val binding: FeedItemBinding, clickListener: ItemClick<MangaItem>) :
    AbstractViewHolder<MangaItem, Unit>(binding.root) {

    init {
        itemView.setOnClickListener {
            boundData?.let { data ->
                clickListener.onClick(it, data)
            }
        }

        itemView.setOnLongClickListener {
            boundData?.let { data ->
                return@setOnLongClickListener clickListener.onLongClick(it, data)
            }

            false
        }
    }

    override fun onBind(data: MangaItem, extra: Unit) {
        with(binding) {
//            imageCover.loadNetWorkImage(data.manga.imageUrl)
            root.loadImage(data.manga.imageUrl)
            mangaName.text = data.manga.title

            if (data.notificationCount > 0) {
                notificationText.visible()
                notificationText.text = data.notificationCount.toString()
            } else {
                notificationText.gone()
            }
        }
    }

    override fun onRecycle() {
//        Glide.with(itemView.context).clear(binding.imageCover)
        binding.root.clearLoading()
        super.onRecycle()
    }

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup, listener: ItemClick<MangaItem>): MangaItemVH {
            return MangaItemVH(FeedItemBinding.inflate(inflater, parent, false), listener)
        }
    }
}