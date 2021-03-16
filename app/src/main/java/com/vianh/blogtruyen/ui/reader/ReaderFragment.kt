package com.vianh.blogtruyen.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.databinding.ReaderFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment
import com.vianh.blogtruyen.utils.getMaxTextureSize
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class ReaderFragment: BaseFragment<ReaderFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ReaderFragmentBinding {
        return ReaderFragmentBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModel<ReaderViewModel> { parametersOf(arguments?.getParcelable(CHAPTER_KEY)) }
    private val tileSize by lazy { getMaxTextureSize().also {
        Timber.d("Max textureSize $it")
    } }


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
        viewModel.error().observe(viewLifecycleOwner, {
            Timber.e(it)
            showToast(it.message)
        })
    }

    private fun setup() {
        with(requireBinding.readerRecycler) {
            OverScrollDecoratorHelper.setUpOverScroll(this, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
            readerAdapter = ReaderAdapter(Glide.with(this), tileSize)
            adapter = readerAdapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        readerAdapter = null
        super.onDestroyView()
    }

    companion object {
        const val CHAPTER_KEY = "CHAPTER_BUNDLE_KEY"
        fun newInstance(chapter: Chapter): ReaderFragment {
            val bundle = Bundle()
            bundle.putParcelable(CHAPTER_KEY, chapter)
            return ReaderFragment().apply {
                arguments = bundle
            }
        }
    }
}