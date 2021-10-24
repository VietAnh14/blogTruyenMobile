package com.vianh.blogtruyen.features.reader

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

abstract class Reader(@LayoutRes layoutRes: Int): Fragment(layoutRes) {
    val callback: ReaderCallback by lazy { parentFragment as ReaderCallback }

    val readerViewModel: ReaderViewModel by lazy { requireParentFragment().getViewModel() }

    abstract fun onContentChange(model: ReaderModel)
    abstract fun toPage(pos: Int, animate: Boolean = true)

    interface ReaderCallback {
        fun onPageChange(pos: Int)
        fun toNextChapter()
        fun toPreChapter()
        fun setControllerVisibility(isVisible: Boolean)
    }
}