package com.vianh.blogtruyen.features.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.ReaderFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.reader.list.ReaderAdapter
import com.vianh.blogtruyen.features.reader.list.TransitionPageVH
import com.vianh.blogtruyen.utils.*
import com.vianh.blogtruyen.views.PinchRecyclerView
import kotlinx.android.synthetic.main.reader_fragment.view.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
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
            arguments?.getParcelable(MANGA_KEY)
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
        viewModel.content.observe(viewLifecycleOwner) { onContentChange(it) }
        viewModel.toast.observe(viewLifecycleOwner) { showToast(it) }
        viewModel.error.observe(viewLifecycleOwner) {
            Timber.e(it)
            showToast(it.message)
        }
    }

    private fun setup() {
        hostActivity?.setupToolbar(requireBinding.toolbar)
        readerDelegate.setUpReader(tileSize)
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
        const val CHAPTER_KEY = "CHAPTER_BUNDLE_KEY"
        const val MANGA_KEY = "MANGA_BUNDLE_KEY"

        fun newInstance(chapter: Chapter, manga: Manga): ReaderFragment {
            val bundle = Bundle()
            bundle.putParcelable(CHAPTER_KEY, chapter)
            bundle.putParcelable(MANGA_KEY, manga)
            return ReaderFragment().apply {
                arguments = bundle
            }
        }
    }
}