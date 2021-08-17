package com.vianh.blogtruyen.features.reader

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.databinding.ReaderFragmentBinding
import com.vianh.blogtruyen.features.reader.list.ReaderAdapter
import com.vianh.blogtruyen.features.reader.list.TransitionPageVH
import com.vianh.blogtruyen.utils.*
import com.vianh.blogtruyen.views.PinchRecyclerView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class ReaderDelegate(private val readerFragment: ReaderFragment, val viewModel: ReaderViewModel) :
    PinchRecyclerView.ReaderCallBack {

    private val binding: ReaderFragmentBinding
        get() = readerFragment.requireBinding

    private var readerAdapter: ReaderAdapter? = null

    @Suppress("DEPRECATION")
    fun setUpReader(tileSize: Int) {
        with(binding) {
            btnNext.setOnClickListener {
                viewModel.toNextChapter()
            }

            btnPrevious.setOnClickListener {
                viewModel.toPreviousChapter()
            }

            ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = insets.systemWindowInsetTop
                }
                insets
            }

            ViewCompat.setOnApplyWindowInsetsListener(chapterController) { v, insets ->
                v.updatePadding(bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
                insets
            }
        }

        with(binding.readerRecycler) {
            setHasFixedSize(true)
            callBack = this@ReaderDelegate
            layoutManager = PreCacheLayoutManager(context)
            val overScrollDecor = OverScrollDecoratorHelper
                .setUpOverScroll(this, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

            overScrollDecor.setOverScrollUpdateListener { _, state, offset ->
                if (offset <= 0) {
                    val pos = adapter?.itemCount ?: return@setOverScrollUpdateListener
                    val transitionVH =
                        findViewHolderForAdapterPosition(pos - 1) as? TransitionPageVH
                    transitionVH?.onOverScroll((-1 * offset).toInt(), state)
                }
            }

            val requestManager = Glide.with(this)
            readerAdapter = ReaderAdapter(requestManager, viewModel, tileSize)
            adapter = readerAdapter
        }
    }

    fun setContent(content: ReaderModel) {
        with(binding) {
            toolbar.title = content.manga.title
            toolbar.subtitle = content.chapter.name

            readerAdapter?.setPages(content.items)
            readerRecycler.scrollToPosition(0)
        }
    }

    private var isControllerShowing = true

    override fun onSingleTap(): Boolean {
        setControllerVisibility(!isControllerShowing)
        return true
    }

    private fun setControllerVisibility(isVisible: Boolean = false) {
        isControllerShowing = isVisible
        if (isVisible) {
            binding.appbar.resetPos()
            binding.chapterController.resetPos()
            readerFragment.hostActivity?.showSystemUI()
        } else {
            binding.appbar.slideUp()
            binding.chapterController.slideDown()
            readerFragment.hostActivity?.hideSystemUI()
        }
    }

    fun cleanUp() {
        readerAdapter = null
    }
}