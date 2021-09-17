package com.vianh.blogtruyen.features.base.list.items

import androidx.annotation.DrawableRes
import com.vianh.blogtruyen.R


data class EmptyItem(@DrawableRes val icon: Int = R.drawable.kurama, val message: String = "No stories found"):
    ListItem {
    override val viewType: Int
        get() = ListItem.EMPTY_ITEM
}