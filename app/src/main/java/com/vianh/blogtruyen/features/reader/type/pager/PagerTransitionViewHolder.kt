package com.vianh.blogtruyen.features.reader.type.pager

import android.view.View
import android.view.ViewGroup
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.PagerTransitionItemBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.reader.list.ReaderItem

class PagerTransitionViewHolder(parent: ViewGroup) :
    AbstractBindingHolder<ReaderItem.TransitionItem, Unit, PagerTransitionItemBinding>(
        R.layout.pager_transition_item,
        parent
    ) {

    override fun onBind(data: ReaderItem.TransitionItem, extra: Unit) {
        val transitionText = if (data.transitionType == ReaderItem.TransitionItem.END_CURRENT) {
            context.getString(R.string.end_chapter_pager, data.chapter.number.toString())
        } else {
            context.getString(R.string.no_next_chapter, data.chapter.number.toString())
        }
        binding.transitionText.text = transitionText
    }

    override fun bindToView(view: View): PagerTransitionItemBinding {
        return PagerTransitionItemBinding.bind(view)
    }
}