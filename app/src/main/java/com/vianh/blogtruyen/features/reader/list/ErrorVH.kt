package com.vianh.blogtruyen.features.reader.list

import android.view.View
import com.vianh.blogtruyen.databinding.ErrorReaderItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.reader.ReaderViewModel

class ErrorVH(val binding: ErrorReaderItemBinding, val viewModel: ReaderViewModel) :
    AbstractViewHolder<ReaderItem.ErrorItem, Unit>(binding.root), View.OnClickListener {

    override fun onBind(data: ReaderItem.ErrorItem, extra: Unit) {
        with(binding) {
            errText.text = data.exception?.message ?: "Unknown error"
            retryButton.setOnClickListener(this@ErrorVH)
        }
    }

    override fun onClick(v: View?) {
        viewModel.loadPages()
    }
}