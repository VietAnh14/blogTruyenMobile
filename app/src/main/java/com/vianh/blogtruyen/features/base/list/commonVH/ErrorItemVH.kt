package com.vianh.blogtruyen.features.base.list.commonVH

import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.ErrorReaderItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.base.list.items.ErrorItem

class ErrorItemVH(parent: ViewGroup, clickListener: ErrorReloadClick):
    AbstractBindingHolder<ErrorItem, Unit, ErrorReaderItemBinding>(R.layout.error_reader_item, parent, { ErrorReaderItemBinding.bind(it) }) {

    init {
        binding.retryButton.setOnClickListener { clickListener.onReload() }
    }

    override fun onBind(data: ErrorItem, extra: Unit) {
        binding.errText.text = data.throwable?.message ?: "Unknown error"
    }

    interface ErrorReloadClick {
        fun onReload()
    }
}