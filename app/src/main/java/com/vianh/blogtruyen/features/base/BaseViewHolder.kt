package com.vianh.blogtruyen.features.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.features.list.ListItem

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(item: ListItem)
    open fun onRecycle() = Unit
}