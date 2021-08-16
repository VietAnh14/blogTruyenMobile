package com.vianh.blogtruyen.features.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.*
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.utils.loadNetWorkImage
import jp.wasabeef.glide.transformations.BlurTransformation

class NewFeedAdapter(private val itemClick: ItemClick) : AbstractAdapter<NewFeedItem, Unit>(Unit) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder<out NewFeedItem, Unit> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            NewFeedItem.UPDATE_ITEM -> UpdateVH.create(inflater, parent, itemClick)
            NewFeedItem.PIN_ITEM -> PinVH.create(inflater, parent, itemClick)
            NewFeedItem.NEW_STORIES_ITEM -> NewStoriesVH.create(inflater, parent, itemClick)
            NewFeedItem.HISTORY_ITEM -> HistoryVH.create(inflater, parent, itemClick)
            NewFeedItem.EMPTY_ITEM -> EmptyFeedVH.create(inflater, parent)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    interface ItemClick {
        fun onItemClick(item: NewFeedItem.MangaItem)
    }
}

class HistoryVH(
    private val binding: MangaGridItemBinding,
    itemClick: NewFeedAdapter.ItemClick
) :
    AbstractViewHolder<NewFeedItem.MangaItem, Unit>(binding.root) {

    init {
        binding.root.updateLayoutParams {
            width = itemView.context.resources.getDimensionPixelSize(R.dimen.feed_history_width)
        }

        itemView.setOnClickListener {
            itemClick.onItemClick(boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: NewFeedItem.MangaItem, extra: Unit) {
        val manga = data.item
        with(binding) {
            imageCover.loadNetWorkImage(manga.imageUrl)
            mangaName.text = manga.title
        }
    }

    companion object {
        fun create(
            inflater: LayoutInflater,
            parent: ViewGroup,
            itemClick: NewFeedAdapter.ItemClick
        ): HistoryVH {
            return HistoryVH(MangaGridItemBinding.inflate(inflater, parent, false), itemClick)
        }
    }
}

class PinVH(private val binding: PinnedItemBinding, itemClick: NewFeedAdapter.ItemClick) :
    AbstractViewHolder<NewFeedItem.MangaItem, Unit>(binding.root) {

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

    companion object {
        fun create(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            itemClick: NewFeedAdapter.ItemClick
        ): PinVH {
            return PinVH(PinnedItemBinding.inflate(layoutInflater, parent, false), itemClick)
        }
    }
}

class EmptyFeedVH(val binding: EmptyMangaItemBinding) :
    AbstractViewHolder<NewFeedItem.EmptyItem, Unit>(binding.root) {
    override fun onBind(data: NewFeedItem.EmptyItem, extra: Unit) {
        with(binding) {
            emptyText.text = data.message
            icon.setImageResource(data.icon)
        }
        return
    }

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup): EmptyFeedVH {
            return EmptyFeedVH(EmptyMangaItemBinding.inflate(inflater, parent, false))
        }
    }
}


class UpdateVH(val binding: MangaGridItemBinding, itemClick: NewFeedAdapter.ItemClick) :
    AbstractViewHolder<NewFeedItem.MangaItem, Unit>(binding.root) {

    init {
        itemView.setOnClickListener {
            itemClick.onItemClick(boundData ?: return@setOnClickListener)
        }
    }

    override fun onBind(data: NewFeedItem.MangaItem, extra: Unit) {
        val manga = data.item
        with(binding) {
            imageCover.loadNetWorkImage(manga.imageUrl)
            mangaName.text = manga.title
        }
    }

    companion object {
        fun create(
            inflater: LayoutInflater,
            parent: ViewGroup,
            itemClick: NewFeedAdapter.ItemClick
        ): UpdateVH {
            return UpdateVH(MangaGridItemBinding.inflate(inflater, parent, false), itemClick)
        }
    }
}

class NewStoriesVH(val binding: NewUploadItemBinding, itemClick: NewFeedAdapter.ItemClick) :
    AbstractViewHolder<NewFeedItem.MangaItem, Unit>(binding.root) {

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

    companion object {
        fun create(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            itemClick: NewFeedAdapter.ItemClick
        ): NewStoriesVH {
            return NewStoriesVH(
                NewUploadItemBinding.inflate(layoutInflater, parent, false),
                itemClick
            )
        }
    }
}