package com.vianh.blogtruyen.features.reader.type.vertical

import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import com.vianh.blogtruyen.utils.gone
import com.vianh.blogtruyen.utils.visible
import me.everything.android.ui.overscroll.IOverScrollState
import kotlin.math.min

class TransitionPageVH(val binding: TransitionPageBinding, val viewModel: ReaderViewModel) :
    AbstractViewHolder<ReaderItem.TransitionItem, Unit>(binding.root) {
    private var canProcess = false

    override fun onBind(data: ReaderItem.TransitionItem, extra: Unit) {
        canProcess = false
        when (data.transitionType) {
            ReaderItem.TransitionItem.END_CURRENT -> setupTransitionPage()
            ReaderItem.TransitionItem.NO_NEXT_CHAPTER -> endNoNextChapter()
        }
    }

    fun onOverScroll(offset: Int, state: Int) {
        if (boundData?.transitionType == ReaderItem.TransitionItem.NO_NEXT_CHAPTER)
            return

        with(binding) {
            if (state == IOverScrollState.STATE_BOUNCE_BACK && canProcess) {
                canProcess = false
                viewModel.toNextChapter()
                return
            }

            val progress = min(offset, 100)
            progressCircular.progress = progress
            if (progress >= 100 && state != IOverScrollState.STATE_BOUNCE_BACK) {
                canProcess = true
                description.setText(R.string.next_chapter_guide)
            } else {
                canProcess = false
                description.setText(R.string.pull_down_guild)
            }
        }
    }

    private fun setupTransitionPage() {
        with(binding) {
            description.text = context.getString(R.string.pull_down_guild, boundData?.chapter?.number.toString())
            nextIcon.visible()
            progressCircular.show()
            progressCircular.progress = 0
        }
    }

    private fun endNoNextChapter() {
        with(binding) {
            description.text = context.getString(R.string.no_next_chapter, boundData?.chapter?.number.toString())
            nextIcon.gone()
            progressCircular.hide()
        }
    }
}