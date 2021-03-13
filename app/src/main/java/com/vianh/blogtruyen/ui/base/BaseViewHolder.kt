package com.vianh.blogtruyen.ui.base

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(position: Int)
}

abstract class BaseBindingViewHolder<out B: ViewDataBinding>(val binding: B) :
    RecyclerView.ViewHolder(binding.root) {

    abstract fun onBind(position: Int)

    open fun onRecycle() {

    }
}