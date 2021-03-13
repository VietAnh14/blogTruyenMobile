package com.vianh.blogtruyen.ui.mangaDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.databinding.ChapterPageFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChapterPageFragment : BaseFragment<ChapterPageFragmentBinding>(), ChapterVH.ChapterClick {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ChapterPageFragmentBinding = ChapterPageFragmentBinding.inflate(inflater, container, false)

    val viewModel by sharedViewModel<MangaDetailsViewModel>(
        owner = {
            ViewModelOwner.from(
                requireParentFragment(),
                parentFragment
            )
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.chapters.observe(viewLifecycleOwner, ::onNewChapters)
    }

    private fun onNewChapters(chapters: List<Chapter>) {
        val adapter = requireBinding.chapterRecycler.adapter as ChapterAdapter
        adapter.submitList(chapters)
    }

    private fun setup() {
        with(requireBinding.chapterRecycler) {
            adapter = ChapterAdapter(this@ChapterPageFragment)
            setHasFixedSize(true)
        }
        viewModel.loadChapters()
    }

    companion object {
        fun newInstance(): ChapterPageFragment {
            return ChapterPageFragment()
        }
    }

    override fun onChapterClick(chapter: Chapter) {

    }
}