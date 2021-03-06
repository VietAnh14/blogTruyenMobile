package com.vianh.blogtruyen.features.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vianh.blogtruyen.databinding.BookmarksFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment

class BookmarksFragment: BaseFragment<BookmarksFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): BookmarksFragmentBinding {
        return BookmarksFragmentBinding.inflate(inflater, container, false)
    }
}