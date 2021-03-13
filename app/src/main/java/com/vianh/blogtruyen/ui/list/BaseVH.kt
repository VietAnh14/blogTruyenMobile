package com.vianh.blogtruyen.ui.list

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseVH<out B: ViewBinding>(val binding: B) :
    RecyclerView.ViewHolder(binding.root) {


    abstract fun onBind(item: ListItem)

    open fun onRecycle() {

    }
}