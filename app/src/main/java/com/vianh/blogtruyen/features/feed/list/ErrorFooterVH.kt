package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.FooterItemErrorBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.feed.NewFeedItem

class ErrorFooterVH(parent: ViewGroup, clickListener: ErrorRetryClick):
    AbstractBindingHolder<NewFeedItem.ErrorFooter, Unit, FooterItemErrorBinding>(R.layout.footer_item_error, parent, { FooterItemErrorBinding.bind(it) }) {
    init {
        binding.btnReload.setOnClickListener { clickListener.onRetryClick() }
    }

    override fun onBind(data: NewFeedItem.ErrorFooter, extra: Unit) {
        binding.errorText.text = data.throwable.message
    }

    interface ErrorRetryClick {
        fun onRetryClick()
    }
}