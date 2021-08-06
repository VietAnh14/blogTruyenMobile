package com.vianh.blogtruyen.features.mangaDetails.mangaInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.ChapterPageFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.download.DownloadIntent
import com.vianh.blogtruyen.features.download.DownloadService
import com.vianh.blogtruyen.features.mangaDetails.MangaDetailsViewModel
import com.vianh.blogtruyen.features.mangaDetails.mangaInfo.adapter.ChapterAdapter
import com.vianh.blogtruyen.features.mangaDetails.mangaInfo.adapter.ChapterItem
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

    private fun onNewChapters(chapters: List<ChapterItem>) {
        chapterAdapter?.submitList(chapters)
    }

    private fun setup() {
        chapterAdapter = ChapterAdapter(this, lifecycleScope)
        headerAdapter = InfoHeaderAdapter(viewModel)
        with(requireBinding.chapterRecycler) {
            adapter = ConcatAdapter(headerAdapter, chapterAdapter)
            setHasFixedSize(true)
        }

        with(requireBinding) {
            swipeRefreshLayout.setOnRefreshListener(this@InfoPageFragment)
            btnRead.setOnClickListener {

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

    override fun onRefresh() {
        viewModel.loadMangaInfo()
    }

    override fun onChapterClick(chapter: ChapterItem) {
        hostActivity?.changeFragment(
            ReaderFragment.newInstance(chapter.chapter, viewModel.currentManga, viewModel.isOffline),
            true
        )
    }

    override fun onStateButtonClick(item: ChapterItem) {
        // Clear chapter list when write to bundle
        val manga = viewModel.currentManga.copy(chapters = emptyList())
        val downloadIntent = DownloadIntent(manga, item.chapter)
        DownloadService.start(requireContext(), downloadIntent)
    }

    companion object {
        fun newInstance(): InfoPageFragment {
            return InfoPageFragment()
        }
    }
}