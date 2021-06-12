package com.vianh.blogtruyen.features.home.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vianh.blogtruyen.databinding.FeedItemBinding
import com.vianh.blogtruyen.features.base.AbstractAdapter
import com.vianh.blogtruyen.features.base.AbstractViewHolder
import com.vianh.blogtruyen.features.list.BaseVH
import com.vianh.blogtruyen.features.list.ListItem
import java.lang.IllegalArgumentException

class MangaFeedAdapter(val itemClick: MangaItemVH.MangaClick): AbstractAdapter<MangaItem, Unit>(Unit) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<MangaItem, Unit> {
        val inflater = LayoutInflater.from(parent.context)
        return MangaItemVH.create(inflater, parent, itemClick)
    }
}