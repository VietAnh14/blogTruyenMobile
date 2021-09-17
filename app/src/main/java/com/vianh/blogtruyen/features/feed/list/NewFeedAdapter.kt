package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.base.list.commonVH.*

class NewFeedAdapter(private val itemClick: ItemClick) : AbstractAdapter<ListItem, Unit>(Unit) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder<out ListItem, Unit> {
        return when (viewType) {
            ListItem.EMPTY_ITEM -> EmptyFeedVH(parent)
            NewFeedItem.PIN_ITEM -> PinVH(parent, itemClick)
            NewFeedItem.UPDATE_ITEM -> UpdateVH(parent, itemClick)
            NewFeedItem.HISTORY_ITEM -> HistoryVH(parent, itemClick)
            NewFeedItem.DETAILS_ITEM -> DetailsVH(parent, itemClick)
            NewFeedItem.NEW_STORIES_ITEM -> NewStoriesVH(parent, itemClick)
            ListItem.ERROR_FOOTER_ITEM -> ErrorFooterVH(parent, itemClick)
            ListItem.ERROR_ITEM -> ErrorItemVH(parent, itemClick)
            ListItem.LOADING_ITEM -> LoadingItemVH(parent)
            ListItem.LOADING_FOOTER_ITEM -> LoadingFooterVH(parent)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    interface ItemClick: ErrorFooterVH.ErrorRetryClick, ErrorItemVH.ErrorReloadClick{
        fun onItemClick(item: NewFeedItem.MangaItem)
    }
}