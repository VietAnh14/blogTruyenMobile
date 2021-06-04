package com.vianh.blogtruyen.features.reader.list

import android.util.Log
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.features.list.BaseVH
import com.vianh.blogtruyen.features.list.ListItem
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import com.vianh.blogtruyen.utils.gone
import com.vianh.blogtruyen.utils.visible
import me.everything.android.ui.overscroll.IOverScrollState
import kotlin.math.min

class TransitionPageVH(binding: TransitionPageBinding, val viewModel: ReaderViewModel) :
    BaseVH<TransitionPageBinding>(binding) {
    var data: ReaderItem.TransitionItem? = null
    private var releaseToLaunch = false

    override fun onBind(item: ListItem) {
        val transitionItem = item as ReaderItem.TransitionItem
        data = transitionItem
        when (transitionItem.transitionType) {
            ReaderItem.TransitionItem.END_CURRENT -> endChapter()
            ReaderItem.TransitionItem.NO_NEXT_CHAPTER -> endNoNextChapter()
        }
    }

    fun onOverScroll(offset: Int, state: Int) {
        if (data?.transitionType == ReaderItem.TransitionItem.NO_NEXT_CHAPTER)
            return

        val progress = min(offset, 100)
        if (state == IOverScrollState.STATE_BOUNCE_BACK && releaseToLaunch) {
            releaseToLaunch = false
            viewModel.toNextChapter()
        } else {
            binding.progressCircular.progress = offset
            if (progress >= 100) {
                releaseToLaunch = true
                binding.description.text = "Release to go to next chapter"
            } else {
                binding.description.text = "End chapter, pull down to go to next chapter"
                releaseToLaunch = false
            }
        }
    }

    private fun endChapter() {
        with(binding) {
            description.text = "End chapter, pull down to go to next chapter"
            nextIcon.visible()
            progressCircular.show()
            progressCircular.progress = 0
        }
    }

    private fun endNoNextChapter() {
        with(binding) {
            description.text = "End chapter, no next chapter available"
            nextIcon.gone()
            progressCircular.hide()
        }
    }
}