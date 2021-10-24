package com.vianh.blogtruyen.features.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.ReaderFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.utils.getMaxTextureSize
import com.vianh.blogtruyen.utils.resetPos
import com.vianh.blogtruyen.utils.slideDown
import com.vianh.blogtruyen.utils.slideUp
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class ReaderFragment : BaseFragment<ReaderFragmentBinding>(), Reader.ReaderCallback {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ReaderFragmentBinding {
        return ReaderFragmentBinding.inflate(inflater, container, false)
    }

    private val tileSize by lazy {
        getMaxTextureSize().also {
            Timber.d("Max textureSize $it")
        }
    }

    private var reader: Reader = VerticalReader.newInstance()

    private val viewModel by viewModel<ReaderViewModel> {
        parametersOf(
            arguments?.getParcelable(CHAPTER_KEY),
            arguments?.getParcelable(MANGA_KEY),
            arguments?.getBoolean(OFFLINE_MODE_KEY)
        )
    }

    private val readerDelegate: ReaderDelegate by lazy {
        ReaderDelegate(this, viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.uiState.observe(viewLifecycleOwner) { onContentChange(it) }
        viewModel.toast.observe(viewLifecycleOwner) { showToast(it) }
    }

    private fun setup() {
        changeReader(VerticalReader.newInstance())
        setupToolbar(requireBinding.toolbar)
        with(requireBinding) {
            btnNext.setOnClickListener {
                viewModel.toNextChapter()
            }

            btnPrevious.setOnClickListener {
                viewModel.toPreviousChapter()
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
        }
    }

    private fun changeReader(reader: Reader) {
        this.reader = reader
        childFragmentManager.commit {
            replace(R.id.reader_container, reader)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }

    override fun onWindowInsetsChange(root: View?, insets: WindowInsetsCompat): WindowInsetsCompat {
        return insets
    }

    private fun onContentChange(content: ReaderModel) {
        reader.onContentChange(content)
    }

    override fun onDestroyView() {
        readerDelegate.cleanUp()
        super.onDestroyView()
    }

    override fun onDestroy() {
        hostActivity?.showSystemUI()
        super.onDestroy()
    }

    override fun onPageChange(pos: Int) {
        val pageNum = viewModel.currentChapter.value.pages.size
        requireBinding.pageText.text = "${pos + 1}/${pageNum}"
    }

    override fun toNextChapter() {
        viewModel.toNextChapter()
    }

    override fun toPreChapter() {
        viewModel.toPreviousChapter()
    }

    override fun setControllerVisibility(isVisible: Boolean) {
        setReaderControlVisibility(isVisible)
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