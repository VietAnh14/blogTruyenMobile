package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import android.widget.FrameLayout
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.feed.NewFeedItem

class LoadingFooterVH(parent: ViewGroup):
    AbstractViewHolder<NewFeedItem.LoadingFooter, Unit>(R.layout.loading_page_item, parent) {

    init {
        itemView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
    }

    override fun onBind(data: NewFeedItem.LoadingFooter, extra: Unit) {

    }
}