package com.vianh.blogtruyen.features.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.ReaderFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.utils.getMaxTextureSize
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class ReaderFragment : BaseFragment<ReaderFragmentBinding>() {
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
        setupToolbar(requireBinding.toolbar)
        readerDelegate.setUpReader(tileSize)
    }

    override fun onWindowInsetsChange(root: View?, insets: WindowInsetsCompat): WindowInsetsCompat {
        return insets
    }

    private fun onContentChange(content: ReaderModel) {
        readerDelegate.setContent(content)
    }

    override fun onDestroyView() {
        readerDelegate.cleanUp()
        super.onDestroyView()
    }

    override fun onDestroy() {
        hostActivity?.showSystemUI()
        super.onDestroy()
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