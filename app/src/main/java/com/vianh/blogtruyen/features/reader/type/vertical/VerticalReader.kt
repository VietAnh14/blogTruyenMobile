package com.vianh.blogtruyen.features.reader.type.vertical

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.base.list.commonVH.ErrorItemVH
import com.vianh.blogtruyen.features.reader.Reader
import com.vianh.blogtruyen.features.reader.ReaderModel
import com.vianh.blogtruyen.utils.ItemPosScrollListener
import com.vianh.blogtruyen.utils.PreCacheLayoutManager
import com.vianh.blogtruyen.utils.SpaceDecorator
import com.vianh.blogtruyen.utils.maxTileSize
import com.vianh.blogtruyen.views.PinchRecyclerView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import timber.log.Timber

class VerticalReader: Reader(R.layout.vertical_reader_layout), ErrorItemVH.ErrorReloadClick {

    private var readerAdapter: ReaderAdapter? = null
    private var pinchRecyclerView: PinchRecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(view, savedInstanceState)
        bindViewModel()
    }

    override fun onDestroyView() {
        readerAdapter = null
        pinchRecyclerView = null
        super.onDestroyView()
    }

    private fun setup(view: View, savedInstanceState: Bundle?) {
        val addSpace = arguments?.getBoolean(ADD_SPACE_KEY) ?: true
        pinchRecyclerView = view.findViewById<PinchRecyclerView>(R.id.reader_recycler)
        with(checkNotNull(pinchRecyclerView)) {
            setHasFixedSize(true)
            if (addSpace) {
                addItemDecoration(SpaceDecorator(requireContext().resources.getDimensionPixelSize(R.dimen.vertical_reader_space)))
            }

            val requestManager = Glide.with(this)
            readerAdapter = ReaderAdapter(requestManager, readerViewModel, maxTileSize, this@VerticalReader)
            adapter = readerAdapter
            layoutManager = PreCacheLayoutManager(requireContext())

            val overScrollDecor = OverScrollDecoratorHelper
                .setUpOverScroll(this, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
            overScrollDecor.setOverScrollUpdateListener { _, state, offset ->
                if (offset <= 0) {
                    val pos = adapter?.itemCount ?: return@setOverScrollUpdateListener
                    val transitionVH =
                        findViewHolderForAdapterPosition(pos - 1) as? TransitionPageVH
                    transitionVH?.onOverScroll((-1 * offset).toInt(), state)
                }
            }

            addOnScrollListener(ItemPosScrollListener {
                readerViewModel.currentPage.value = it
            })

        }
    }

    private fun bindViewModel() {
        readerViewModel.uiState.observe(viewLifecycleOwner) {
            onChapterContentChange(it)
            pinchRecyclerView?.scrollToPosition(readerViewModel.currentPage.value)
        }
    }

    private fun onChapterContentChange(model: ReaderModel) {
        readerAdapter?.setPages(model.items)
    }

    override fun toPage(pos: Int, animate: Boolean) {
        if (animate) {
            pinchRecyclerView?.smoothScrollToPosition(pos)
        } else {
            pinchRecyclerView?.scrollToPosition(pos)
        }
    }

    override fun onReload() {
        readerViewModel.loadPages()
    }

    companion object {
        const val ADD_SPACE_KEY = "ADD_SPACE"
        fun newInstance(addSpace: Boolean = true): VerticalReader {
            val bundle = Bundle(1).apply {
                putBoolean(ADD_SPACE_KEY, addSpace)
            }
            return VerticalReader().also { it.arguments = bundle }
        }
    }
}