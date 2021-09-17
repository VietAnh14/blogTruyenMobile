package com.vianh.blogtruyen.features.base.list.commonVH

import android.view.ViewGroup
import android.widget.FrameLayout
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.items.LoadingFooterItem

class LoadingFooterVH(parent: ViewGroup):
    AbstractViewHolder<LoadingFooterItem, Unit>(R.layout.loading_page_item, parent) {

    init {
        itemView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
    }

    override fun onBind(data: LoadingFooterItem, extra: Unit) {

    }
}