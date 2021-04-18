package com.vianh.blogtruyen.ui.mangaDetails.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vianh.blogtruyen.databinding.CommentPageFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment
import com.vianh.blogtruyen.ui.mangaDetails.MangaDetailsViewModel
import com.vianh.blogtruyen.utils.ScrollLoadMore
import org.koin.androidx.viewmodel.ext.android.getViewModel

class CommentPageFragment: BaseFragment<CommentPageFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): CommentPageFragmentBinding {
        return CommentPageFragmentBinding.inflate(inflater, container, false)
    }

    private val viewModel: MangaDetailsViewModel by lazy {
        requireParentFragment().getViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.comments.observe(viewLifecycleOwner, {
            (requireBinding.commentRecycler.adapter as CommentAdapter).submitList(it)
        })
        viewModel.loadComments()
    }

    private fun setup() {
        with(requireBinding.commentRecycler) {
            adapter = CommentAdapter()
            clearOnScrollListeners()
            addOnScrollListener(ScrollLoadMore(3, ) {
                viewModel.loadComments()
            })
        }
    }

    companion object {
        fun newInstance(): CommentPageFragment {
            return CommentPageFragment()
        }
    }
}