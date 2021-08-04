package com.vianh.blogtruyen.features.home.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vianh.blogtruyen.features.list.AbstractAdapter
import com.vianh.blogtruyen.features.list.AbstractViewHolder

class MangaFeedAdapter(val itemClick: MangaItemVH.MangaClick): AbstractAdapter<MangaItem, Unit>(Unit) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<MangaItem, Unit> {
        val inflater = LayoutInflater.from(parent.context)
        return MangaItemVH.create(inflater, parent, itemClick)
    }
}