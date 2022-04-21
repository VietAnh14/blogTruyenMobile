package com.vianh.blogtruyen.features.base.list.commonVH

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.items.LoadingItem
import com.vianh.blogtruyen.utils.ext.inflate

class LoadingItemVH(parent: ViewGroup): AbstractViewHolder<LoadingItem, Unit>(parent.inflate(R.layout.loading_page_item)) {

    override fun onBind(data: LoadingItem, extra: Unit) {

    }

}