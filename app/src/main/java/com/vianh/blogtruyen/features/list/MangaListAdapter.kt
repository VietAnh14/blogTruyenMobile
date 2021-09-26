package com.vianh.blogtruyen.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.prefs.ListMode
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.ItemClick
import com.vianh.blogtruyen.features.base.list.commonVH.EmptyFeedVH
import com.vianh.blogtruyen.features.base.list.commonVH.LoadingFooterVH
import com.vianh.blogtruyen.features.base.list.commonVH.LoadingItemVH
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.feed.list.DetailsVH

class MangaListAdapter(val itemClick: ItemClick<MangaItem>): AbstractAdapter<ListItem, Unit>(Unit) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<out ListItem, Unit> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MangaItem.MANGA_GRID_ITEM -> MangaItemVH.create(inflater, parent, itemClick)
            MangaItem.MANGA_DETAIL_LIST_ITEM -> DetailsVH(parent, itemClick)
            ListItem.EMPTY_ITEM -> EmptyFeedVH(parent)
            ListItem.LOADING_FOOTER_ITEM -> LoadingFooterVH(parent)
            ListItem.LOADING_ITEM -> LoadingItemVH(parent)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }
}