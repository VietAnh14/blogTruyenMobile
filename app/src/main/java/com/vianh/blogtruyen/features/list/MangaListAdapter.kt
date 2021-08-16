package com.vianh.blogtruyen.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder

class MangaListAdapter(val itemClick: MangaItemVH.MangaClick): AbstractAdapter<MangaItem, Unit>(Unit) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<MangaItem, Unit> {
        val inflater = LayoutInflater.from(parent.context)
        return MangaItemVH.create(inflater, parent, itemClick)
    }
}