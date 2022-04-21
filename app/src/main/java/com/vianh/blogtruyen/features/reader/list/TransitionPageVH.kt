package com.vianh.blogtruyen.features.reader.list

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.prefs.ReaderMode
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.features.base.list.AbstractBindingHolder
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import com.vianh.blogtruyen.utils.ext.gone
import com.vianh.blogtruyen.utils.ext.visible
import me.everything.android.ui.overscroll.IOverScrollState
import kotlin.math.min

class TransitionPageVH(val parent: ViewGroup, val viewModel: ReaderViewModel, val readerMode: ReaderMode) :
    AbstractBindingHolder<ReaderItem.TransitionItem, Unit, TransitionPageBinding>(R.layout.transition_page, parent) {

    init {
        if (readerMode == ReaderMode.HORIZON) {
            itemView.updateLayoutParams { height = ViewGroup.LayoutParams.MATCH_PARENT }
            binding.description.setText(R.string.swipe_guide)
        } else {
            itemView.updateLayoutParams { height = context.resources.getDimensionPixelSize(R.dimen.transition_page_height) }
            binding.description.setText(R.string.pull_down_guide)
        }
    }

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
                description.text = context.getString(getGuideRes(), boundData?.chapter?.name)
            }
        }
    }

    private fun setupTransitionPage() {
        with(binding) {
            description.text = context.getString(getGuideRes(), boundData?.chapter?.number.toString())
            nextIcon.visible()
            progressCircular.show()
            progressCircular.progress = 0
        }
    }

    private fun getGuideRes(): Int {
        return if (readerMode == ReaderMode.HORIZON) {
            R.string.swipe_guide
        } else {
            R.string.pull_down_guide
        }
    }

    private fun endNoNextChapter() {
        with(binding) {
            description.text = context.getString(R.string.no_next_chapter, boundData?.chapter?.number.toString())
            nextIcon.gone()
            progressCircular.hide()
        }
    }

    override fun bindToView(view: View): TransitionPageBinding {
        return TransitionPageBinding.bind(view)
    }
}