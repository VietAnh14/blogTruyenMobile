package com.vianh.blogtruyen.features.reader

import android.os.Bundle
import android.view.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.prefs.AppSettings
import com.vianh.blogtruyen.data.prefs.ReaderMode
import com.vianh.blogtruyen.databinding.ReaderFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.reader.type.pager.PagerReader
import com.vianh.blogtruyen.features.reader.type.vertical.VerticalReader
import com.vianh.blogtruyen.utils.resetPos
import com.vianh.blogtruyen.utils.slideDown
import com.vianh.blogtruyen.utils.slideUp
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class ReaderFragment : BaseFragment<ReaderFragmentBinding>(), Reader.ReaderContract, SettingPopupWindow.Callback, ReaderContainer.Callback {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ReaderFragmentBinding {
        return ReaderFragmentBinding.inflate(inflater, container, false)
    }

    private val appSettings by inject<AppSettings>()

    override val readerViewModel by viewModel<ReaderViewModel> {
        parametersOf(
            arguments?.getParcelable(CHAPTER_KEY),
            arguments?.getParcelable(MANGA_KEY),
            arguments?.getBoolean(OFFLINE_MODE_KEY)
        )
    }

    private val currentReader
        get() = childFragmentManager.findFragmentById(R.id.reader_container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        bindViewModel()
    }

    private fun bindViewModel() {
        readerViewModel.uiState.observe(viewLifecycleOwner, ::onContentChange)
        readerViewModel.toast.observe(viewLifecycleOwner, this::showToast)
        readerViewModel.controllerVisibility.observe(viewLifecycleOwner, ::setReaderControlVisibility)
        readerViewModel.pageString.observe(viewLifecycleOwner) { requireBinding.pageText.text = it }
    }

    private fun setup() {
        setupToolbar(requireBinding.toolbar)
        with(requireBinding) {
            btnNext.setOnClickListener {
                readerViewModel.toNextChapter()
            }

            btnPrevious.setOnClickListener {
                readerViewModel.toPreviousChapter()
            }

            ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = insets.systemWindowInsetTop
                }
                insets
            }

            ViewCompat.setOnApplyWindowInsetsListener(chapterController) { v, insets ->
                v.updatePadding(bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
                insets
            }

            readerContainer.callback = this@ReaderFragment
        }

        changeReader(getReader(appSettings.getReaderMode()))
        onKeepScreenOnChange(appSettings.getKeepScreenOn())
    }

    private fun getReader(mode: ReaderMode): Reader {
        return when(mode) {
            ReaderMode.HORIZON -> PagerReader.newInstance()
            ReaderMode.VERTICAL -> VerticalReader.newInstance()
        }
    }

    private fun changeReader(reader: Reader) {
        // Avoid leak when replace 2 fragment with same type
        if (currentReader?.javaClass == reader.javaClass) {
            return
        }

        childFragmentManager.commit {
            replace(R.id.reader_container, reader)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }

    override fun onWindowInsetsChange(root: View?, insets: WindowInsetsCompat): WindowInsetsCompat {
        return insets
    }

    private fun onContentChange(content: ReaderModel) {
        with(requireBinding) {
            toolbar.title = content.manga.title
            toolbar.subtitle = content.chapter.name
        }
    }

    private fun showSettingMenu(anchor: View) {
        SettingPopupWindow.show(anchor, appSettings, this)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.setting_item -> {
                val anchor = requireView().findViewById<View>(R.id.setting_item) ?: return false
                showSettingMenu(anchor)
                true
            }

            else -> super.onMenuItemClick(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        hostActivity?.showSystemUI()
        super.onDestroy()
    }

    private fun setReaderControlVisibility(isVisible: Boolean = false) {
        if (isVisible) {
            requireBinding.appbar.resetPos()
            requireBinding.chapterController.resetPos()
            hostActivity?.showSystemUI()
        } else {
            requireBinding.appbar.slideUp()
            requireBinding.chapterController.slideDown()
            hostActivity?.hideSystemUI()
        }
    }

    override fun onReaderModeSelected(mode: ReaderMode) {
        changeReader(getReader(mode))
    }

    override fun onKeepScreenOnChange(keepScreenOn: Boolean) {
        requireBinding.readerContainer.keepScreenOn = keepScreenOn
    }

    override fun onSingleTap(e: MotionEvent?): Boolean {
        readerViewModel.toggleControllerVisibility()
        return true
    }

    companion object {
        private const val CHAPTER_KEY = "CHAPTER_BUNDLE_KEY"
        private const val MANGA_KEY = "MANGA_BUNDLE_KEY"
        private const val OFFLINE_MODE_KEY = "OFFLINE"

        fun newInstance(chapter: Chapter, manga: Manga, isOffline: Boolean = false): ReaderFragment {
            val bundle = Bundle(3)
            bundle.putParcelable(CHAPTER_KEY, chapter)
            bundle.putParcelable(MANGA_KEY, manga)
            bundle.putBoolean(OFFLINE_MODE_KEY, isOffline)
            return ReaderFragment().apply {
                arguments = bundle
            }
        }
    }
}