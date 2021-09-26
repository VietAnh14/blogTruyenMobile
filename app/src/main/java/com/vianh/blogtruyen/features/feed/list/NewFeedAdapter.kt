package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.ItemClick
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.base.list.commonVH.*
import com.vianh.blogtruyen.features.list.MangaItem

class NewFeedAdapter(private val callback: Callbacks) : AbstractAdapter<ListItem, Unit>(Unit) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder<out ListItem, Unit> {
        return when (viewType) {
            ListItem.EMPTY_ITEM -> EmptyFeedVH(parent)
            NewFeedItem.PIN_ITEM -> PinVH(parent, callback)
            NewFeedItem.UPDATE_ITEM -> UpdateVH(parent, callback)
            NewFeedItem.HISTORY_ITEM -> HistoryVH(parent, callback)
            NewFeedItem.DETAILS_ITEM -> DetailsVH(parent, callback)
            NewFeedItem.NEW_STORIES_ITEM -> NewStoriesVH(parent, callback)
            ListItem.ERROR_FOOTER_ITEM -> ErrorFooterVH(parent, callback)
            ListItem.ERROR_ITEM -> ErrorItemVH(parent, callback)
            ListItem.LOADING_ITEM -> LoadingItemVH(parent)
            ListItem.LOADING_FOOTER_ITEM -> LoadingFooterVH(parent)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    interface Callbacks: ErrorFooterVH.ErrorRetryClick, ErrorItemVH.ErrorReloadClick, ItemClick<MangaItem>
}