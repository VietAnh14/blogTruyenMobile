package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.feed.NewFeedItem

class NewFeedAdapter(private val itemClick: ItemClick) : AbstractAdapter<NewFeedItem, Unit>(Unit) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder<out NewFeedItem, Unit> {
        return when (viewType) {
            NewFeedItem.EMPTY_ITEM -> EmptyFeedVH(parent)
            NewFeedItem.PIN_ITEM -> PinVH(parent, itemClick)
            NewFeedItem.UPDATE_ITEM -> UpdateVH(parent, itemClick)
            NewFeedItem.HISTORY_ITEM -> HistoryVH(parent, itemClick)
            NewFeedItem.DETAILS_ITEM -> DetailsVH(parent, itemClick)
            NewFeedItem.NEW_STORIES_ITEM -> NewStoriesVH(parent, itemClick)
            NewFeedItem.ERROR_FOOTER -> ErrorFooterVH(parent, itemClick)
            NewFeedItem.ERROR_ITEM -> ErrorItemVH(parent, itemClick)
            NewFeedItem.LOADING_ITEM -> LoadingItemVH(parent)
            NewFeedItem.LOADING_FOOTER -> LoadingFooterVH(parent)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    interface ItemClick: ErrorFooterVH.ErrorRetryClick, ErrorItemVH.ErrorReloadClick{
        fun onItemClick(item: NewFeedItem.MangaItem)
    }
}