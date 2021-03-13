package com.vianh.blogtruyen.ui.mangaDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vianh.blogtruyen.databinding.CommentPageFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment

class CommentPageFragment: BaseFragment<CommentPageFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): CommentPageFragmentBinding {
        return CommentPageFragmentBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance(): CommentPageFragment {
            return CommentPageFragment()
        }
    }
}