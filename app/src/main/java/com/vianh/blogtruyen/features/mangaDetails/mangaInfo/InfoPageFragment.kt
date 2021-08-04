package com.vianh.blogtruyen.features.mangaDetails.mangaInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.ChapterPageFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.download.DownloadIntent
import com.vianh.blogtruyen.features.download.DownloadService
import com.vianh.blogtruyen.features.mangaDetails.MangaDetailsViewModel
import com.vianh.blogtruyen.features.mangaDetails.mangaInfo.adapter.ChapterAdapter
import com.vianh.blogtruyen.features.mangaDetails.mangaInfo.adapter.InfoHeaderAdapter
import com.vianh.blogtruyen.features.reader.ReaderFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class InfoPageFragment : BaseFragment<ChapterPageFragmentBinding>(), ChapterVH.ChapterClick,
    SwipeRefreshLayout.OnRefreshListener {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ChapterPageFragmentBinding = ChapterPageFragmentBinding.inflate(inflater, container, false)

    private val viewModel: MangaDetailsViewModel by lazy {
        requireParentFragment().getViewModel()
    }

    private var chapterAdapter: ChapterAdapter? = null
    private var headerAdapter: InfoHeaderAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.chapters.observe(viewLifecycleOwner, this::onNewChapters)
        viewModel.manga.observe(viewLifecycleOwner, this::onMangaChange)
        viewModel.isLoading.observe(viewLifecycleOwner, this::onLoadingChange)
        viewModel.isFavorite.observe(viewLifecycleOwner, this::onFavoriteStateChange)

        viewModel.error.observe(viewLifecycleOwner, { showToast(it.message) })
    }

    private fun onFavoriteStateChange(isFavorite: Boolean) {
        requireBinding.actionFollow.isChecked = isFavorite
    }

    private fun onMangaChange(manga: Manga) {
        Timber.d("Render manga")
        headerAdapter?.submitItem(manga)
    }

    private fun onNewChapters(chapters: List<Chapter>) {
        chapterAdapter?.submitList(chapters)
    }

    private fun setup() {
        chapterAdapter = ChapterAdapter(this)
        headerAdapter = InfoHeaderAdapter(viewModel)
        with(requireBinding.chapterRecycler) {
            adapter = ConcatAdapter(headerAdapter, chapterAdapter)
            setHasFixedSize(true)
        }

        with(requireBinding) {
            swipeRefreshLayout.setOnRefreshListener(this@InfoPageFragment)
            btnRead.setOnClickListener {
                val chapter = viewModel.currentManga.chapters.first()

                // Clear chapter list when write to bundle
                val manga = viewModel.currentManga.copy(chapters = emptyList())
                val downloadIntent = DownloadIntent(manga, chapter)
                DownloadService.start(requireContext(), downloadIntent)
            }

            actionFollow.setOnClickListener {
                viewModel.toggleFavorite(!requireBinding.actionFollow.isChecked)
            }
        }

    }

    private fun onLoadingChange(isLoading: Boolean) {
        requireBinding.swipeRefreshLayout.isRefreshing = isLoading
    }

    override fun onDestroyView() {
        chapterAdapter = null
        headerAdapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): InfoPageFragment {
            return InfoPageFragment()
        }
    }

    override fun onChapterClick(chapter: Chapter) {
        hostActivity?.changeFragment(ReaderFragment.newInstance(chapter, viewModel.currentManga), true)
    }

    override fun onRefresh() {
        viewModel.loadMangaInfo()
    }
}