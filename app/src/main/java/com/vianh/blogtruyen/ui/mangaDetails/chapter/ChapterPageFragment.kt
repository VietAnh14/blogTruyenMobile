package com.vianh.blogtruyen.ui.mangaDetails.chapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.databinding.ChapterPageFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment
import com.vianh.blogtruyen.ui.mangaDetails.MangaDetailsViewModel
import com.vianh.blogtruyen.ui.reader.ReaderFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ChapterPageFragment : BaseFragment<ChapterPageFragmentBinding>(), ChapterVH.ChapterClick {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ChapterPageFragmentBinding = ChapterPageFragmentBinding.inflate(inflater, container, false)

    private val viewModel: MangaDetailsViewModel by lazy {
        requireParentFragment().getViewModel()
    }

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
        hostActivity?.changeFragment(ReaderFragment.newInstance(chapter, viewModel.manga), true)
    }
}