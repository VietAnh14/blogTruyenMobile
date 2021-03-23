package com.vianh.blogtruyen.ui.reader

import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.ui.list.BaseVH
import com.vianh.blogtruyen.ui.list.ListItem
import com.vianh.blogtruyen.utils.gone
import com.vianh.blogtruyen.utils.visible
import me.everything.android.ui.overscroll.IOverScrollState
import kotlin.math.min

class TransitionPageVH(binding: TransitionPageBinding, val viewModel: ReaderViewModel): BaseVH<TransitionPageBinding>(binding) {
    var data: TransitionPageItem? = null
    var releaseToLaunch = false

    override fun onBind(item: ListItem) {
        val transitionItem = item as TransitionPageItem
        data = transitionItem
        when (transitionItem.transitionType) {
            TransitionPageItem.END_CURRENT -> endChapter()
            TransitionPageItem.NO_NEXT_CHAPTER -> endNoNextChapter()
        }
    }

    fun onOverScroll(offset: Int, state: Int) {
        val progress = min(offset, 100)
        if (state == IOverScrollState.STATE_BOUNCE_BACK && releaseToLaunch) {
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