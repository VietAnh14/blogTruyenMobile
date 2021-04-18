package com.vianh.blogtruyen.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.ReaderFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment
import com.vianh.blogtruyen.ui.reader.list.ReaderAdapter
import com.vianh.blogtruyen.ui.reader.list.TransitionPageVH
import com.vianh.blogtruyen.utils.PreCacheLayoutManager
import com.vianh.blogtruyen.utils.getMaxTextureSize
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
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

    private val viewModel by viewModel<ReaderViewModel> {
        parametersOf(
            arguments?.getParcelable(CHAPTER_KEY),
            arguments?.getParcelable(MANGA_KEY)
        )
    }
    private val tileSize by lazy {
        getMaxTextureSize().also {
            Timber.d("Max textureSize $it")
        }
    }

    var readerAdapter: ReaderAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.pages.observe(viewLifecycleOwner, {
            readerAdapter?.setPages(it)
        })
        viewModel.error.observe(viewLifecycleOwner, {
            Timber.e(it)
            showToast(it.message)
        })
    }

    private fun setup() {
        hostActivity?.hideSystemUI()

        with(requireBinding.readerRecycler) {
            layoutManager = PreCacheLayoutManager(context)
            val overScrollDecor = OverScrollDecoratorHelper.setUpOverScroll(
                this,
                OverScrollDecoratorHelper.ORIENTATION_VERTICAL
            )

            overScrollDecor.setOverScrollUpdateListener { _, state, offset ->
                if (offset <= 0) {
                    val pos = adapter?.itemCount ?: return@setOverScrollUpdateListener
                    val transitionVH =
                        findViewHolderForAdapterPosition(pos - 1) as? TransitionPageVH
                    transitionVH?.onOverScroll((-1 * offset).toInt(), state)
                }
            }

            val requestManager = Glide.with(this)
            readerAdapter = ReaderAdapter(requestManager, viewModel, tileSize)
            adapter = readerAdapter
            setHasFixedSize(true)
        }

        hostActivity?.hideBottomNav()
    }

    override fun onDestroyView() {
        readerAdapter = null
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