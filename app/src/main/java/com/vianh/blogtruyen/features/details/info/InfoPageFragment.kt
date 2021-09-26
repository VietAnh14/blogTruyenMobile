package com.vianh.blogtruyen.features.details.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialcab.attached.AttachedCab
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.createCab
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.ChapterPageFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.download.DownloadIntent
import com.vianh.blogtruyen.features.download.DownloadService
import com.vianh.blogtruyen.features.details.MangaDetailsViewModel
import com.vianh.blogtruyen.features.details.info.adapter.*
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
    private var chapterHeaderAdapter: ChapterHeaderAdapter? = null

    private var cab: AttachedCab? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.chapterItems.observe(viewLifecycleOwner, this::onNewChapters)
        viewModel.headerItem.observe(viewLifecycleOwner, this::onHeaderChange)
        viewModel.manga.observe(viewLifecycleOwner, this::onMangaChange)
        viewModel.isLoading.observe(viewLifecycleOwner, this::onLoadingChange)
        viewModel.isFavorite.observe(viewLifecycleOwner, this::onFavoriteStateChange)
        viewModel.toReaderEvent.observe(viewLifecycleOwner, this::toReaderFragment)
        viewModel.readButtonState.observe(viewLifecycleOwner, this::onButtonStateChange)
        viewModel.onNewPageSelected.observe(viewLifecycleOwner, this::onNewPageSelected)

        viewModel.error.observe(viewLifecycleOwner, { showToast(it.message) })
    }

    private fun onNewPageSelected(position: Int) {
        cab?.destroy()
    }

    private fun onButtonStateChange(buttonState: Pair<Boolean, Int>) {
        with(requireBinding.btnRead) {
            isEnabled = buttonState.first
            setText(buttonState.second)
        }
    }

    private fun onHeaderChange(headerItem: HeaderItem) {
        chapterHeaderAdapter?.submitList(listOf(headerItem))
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
        chapterAdapter = ChapterAdapter(this, viewLifecycleOwner.lifecycleScope)
        headerAdapter = InfoHeaderAdapter()
        chapterHeaderAdapter = ChapterHeaderAdapter(viewModel)
        with(requireBinding.chapterRecycler) {
            itemAnimator = null
            adapter = ConcatAdapter(headerAdapter, chapterHeaderAdapter, chapterAdapter)
            setHasFixedSize(true)
        }

        with(requireBinding) {
            swipeRefreshLayout.setOnRefreshListener(this@InfoPageFragment)
            btnRead.setOnClickListener {
                viewModel.continueReading()
            }

            actionFollow.setOnClickListener {
                viewModel.toggleFavorite()
            }
        }

    }

    private fun onLoadingChange(isLoading: Boolean) {
        requireBinding.swipeRefreshLayout.isRefreshing = isLoading
    }

    private fun toReaderFragment(chapter: Chapter) {
        hostActivity?.changeFragment(
            ReaderFragment
            .newInstance(chapter, viewModel.currentManga, viewModel.isOffline), true)
    }

    private fun updateCab() {
        val selectedChapters = chapterAdapter?.selectedChapters ?: return
        if (selectedChapters.isEmpty()) {
            cab?.destroy()
            return
        }

        if (cab == null) {
            cab = createCab(R.id.cab_container) {
                title(literal = selectedChapters.size.toString())
                menu(R.menu.chapter_selection_menu)
                onDestroy {
                    cab = null
                    chapterAdapter?.clearSelections()
                    true
                }
                onSelection { onMenuItemClick(it) }
                fadeIn()
            }
        }

        cab?.title(literal = selectedChapters.size.toString())
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_all -> {
                chapterAdapter?.selectChapters(viewModel.chapters.value)
                updateCab()
                true
            }

            R.id.download -> {
                val chapters = chapterAdapter?.selectedChapters ?: return true
                DownloadService.download(requireContext(), viewModel.currentManga, chapters.toList())
                cab?.destroy()
                true
            }
            else -> super.onMenuItemClick(item)
        }
    }

    override fun onDestroyView() {
        chapterAdapter = null
        headerAdapter = null
        chapterHeaderAdapter = null
        super.onDestroyView()
    }

    override fun onRefresh() {
        viewModel.loadMangaInfo()
    }

    override fun onChapterClick(chapter: ChapterItem) {
        if (chapterAdapter?.hasSelectedChapters() == true) {
            chapterAdapter?.selectChapter(chapter)
        } else {
            toReaderFragment(chapter.chapter)
        }

        updateCab()
    }

    override fun onChapterLongClick(chapter: ChapterItem, view: View, position: Int) {
        chapterAdapter?.selectChapter(chapter)
        updateCab()
    }

    override fun onStateButtonClick(item: ChapterItem) {
        DownloadService.download(requireContext(), viewModel.currentManga, listOf(item.chapter))
    }

    companion object {
        fun newInstance(): InfoPageFragment {
            return InfoPageFragment()
        }
    }
}