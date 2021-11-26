package com.vianh.blogtruyen.features.reader

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.prefs.AppSettings
import com.vianh.blogtruyen.data.prefs.ReaderMode
import com.vianh.blogtruyen.databinding.ReaderFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.main.MainActivity
import com.vianh.blogtruyen.features.reader.type.pager.PagerReader
import com.vianh.blogtruyen.features.reader.type.vertical.VerticalReader
import com.vianh.blogtruyen.utils.resetPos
import com.vianh.blogtruyen.utils.slideDown
import com.vianh.blogtruyen.utils.slideUp
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ReaderFragment : BaseFragment<ReaderFragmentBinding>(), Reader.ReaderContract, SettingPopupWindow.Callback, ReaderContainer.Callback {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ReaderFragmentBinding {
        return ReaderFragmentBinding.inflate(inflater, container, false)
    }

    private val appSettings by inject<AppSettings>()
    private var lastSavedState: Bundle? = null

    override val readerViewModel by viewModel<ReaderViewModel> { parametersOf(getRestoreState()) }

    private val currentReader
        get() = childFragmentManager.findFragmentById(R.id.reader_container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lastSavedState = savedInstanceState
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

            readerContainer.callback = this@ReaderFragment
        }

        changeReader(getReader(appSettings.getReaderMode()))
        onKeepScreenOnChange(appSettings.getKeepScreenOn())
    }

    private fun getReader(mode: ReaderMode): Reader {
        return when(mode) {
            ReaderMode.HORIZON -> PagerReader.newInstance()
            ReaderMode.VERTICAL -> VerticalReader.newInstance()
            ReaderMode.CONTINUOUS_VERTICAL -> VerticalReader.newInstance(false)
        }
    }

    private fun changeReader(reader: Reader, force: Boolean = false) {
        // Avoid leak when replace 2 fragment with same type
        if (currentReader?.javaClass == reader.javaClass && !force) {
            return
        }

        childFragmentManager.commit {
            replace(R.id.reader_container, reader)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        readerViewModel.saveReaderState(outState)
        super.onSaveInstanceState(outState)
        lastSavedState = outState
    }

    private fun getRestoreState(): ReaderState {
        val bundle = lastSavedState ?: arguments
        val state = bundle?.getParcelable<ReaderState>(READER_STATE_KEY)
        return state ?: throw IllegalStateException("No reader state")
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat): WindowInsetsCompat {
        val barInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        requireBinding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = barInsets.top
        }

        requireBinding.dummyView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = barInsets.bottom
        }
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
        val force = mode == ReaderMode.VERTICAL || mode == ReaderMode.CONTINUOUS_VERTICAL
        changeReader(getReader(mode), force)
    }

    override fun onKeepScreenOnChange(keepScreenOn: Boolean) {
        requireBinding.readerContainer.keepScreenOn = keepScreenOn
    }

    override fun onSingleTap(e: MotionEvent?): Boolean {
        readerViewModel.toggleControllerVisibility()
        return true
    }

    companion object {
        const val READER_STATE_KEY = "READER_STATE"

        fun newInstance(readerState: ReaderState): ReaderFragment {
            val bundle = Bundle(1).apply {
                putParcelable(READER_STATE_KEY, readerState)
            }

            return ReaderFragment().apply {
                arguments = bundle
            }
        }
    }
}