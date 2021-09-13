package com.vianh.blogtruyen.features.feed.list

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.ErrorReaderItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.feed.NewFeedItem

class ErrorItemVH(parent: ViewGroup, clickListener: ErrorReloadClick):
    AbstractBindingHolder<NewFeedItem.ErrorFooter, Unit, ErrorReaderItemBinding>(R.layout.error_reader_item, parent, { ErrorReaderItemBinding.bind(it) }) {

    init {
        binding.retryButton.setOnClickListener { clickListener.onReload() }
    }

    override fun onBind(data: NewFeedItem.ErrorFooter, extra: Unit) {
        binding.errText.text = data.throwable.message
    }

    interface ErrorReloadClick {
        fun onReload()
    }
}