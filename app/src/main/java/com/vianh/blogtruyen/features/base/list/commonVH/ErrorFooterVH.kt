package com.vianh.blogtruyen.features.base.list.commonVH

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.FooterItemErrorBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.base.list.items.ErrorFooterItem

class ErrorFooterVH(parent: ViewGroup, clickListener: ErrorRetryClick):
    AbstractBindingHolder<ErrorFooterItem, Unit, FooterItemErrorBinding>(R.layout.footer_item_error, parent, { FooterItemErrorBinding.bind(it) }) {
    init {
        binding.btnReload.setOnClickListener { clickListener.onRetryClick() }
    }

    override fun onBind(data: ErrorFooterItem, extra: Unit) {
        binding.errorText.text = data.throwable.message
    }

    interface ErrorRetryClick {
        fun onRetryClick()
    }
}