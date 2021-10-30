package com.vianh.blogtruyen.features.reader

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

abstract class Reader(@LayoutRes layoutRes: Int): Fragment(layoutRes) {

    val readerViewModel: ReaderViewModel by lazy { requireParentFragment().getViewModel() }

    abstract fun toPage(pos: Int, animate: Boolean = true)

    interface ReaderContract {
        val readerViewModel: ReaderViewModel
    }
}