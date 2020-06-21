package com.vianh.blogtruyen.ui.base

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(position: Int)
}

abstract class BaseBindingViewHolder<out T: ViewDataBinding>(private val binding: T) : RecyclerView.ViewHolder(binding.root) {
    fun getBinding(): T = binding
    abstract fun onBind(position: Int)
}