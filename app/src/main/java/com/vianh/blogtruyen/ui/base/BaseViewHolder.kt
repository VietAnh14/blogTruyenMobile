package com.vianh.blogtruyen.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.ui.list.ListItem

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(item: ListItem)
    open fun onRecycle() = Unit
}