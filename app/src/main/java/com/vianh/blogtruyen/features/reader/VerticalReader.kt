package com.vianh.blogtruyen.features.reader

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.base.list.commonVH.ErrorItemVH
import com.vianh.blogtruyen.features.reader.list.ReaderAdapter
import com.vianh.blogtruyen.features.reader.list.TransitionPageVH
import com.vianh.blogtruyen.utils.ItemPosScrollListener
import com.vianh.blogtruyen.utils.PreCacheLayoutManager
import com.vianh.blogtruyen.utils.maxTileSize
import com.vianh.blogtruyen.views.PinchRecyclerView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import timber.log.Timber

class VerticalReader: Reader(R.layout.vertical_reader_layout), PinchRecyclerView.ReaderCallBack, ErrorItemVH.ErrorReloadClick {

    private var readerAdapter: ReaderAdapter? = null
    private var pinchRecyclerView: PinchRecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(view, savedInstanceState)
        bindViewModel()
    }

    override fun onDestroyView() {
        Timber.e("Destroyed ${hashCode()}")
        readerAdapter = null
        pinchRecyclerView = null
        super.onDestroyView()
    }

    private fun setup(view: View, savedInstanceState: Bundle?) {
        pinchRecyclerView = view.findViewById<PinchRecyclerView>(R.id.reader_recycler)
        with(checkNotNull(pinchRecyclerView)) {
            setHasFixedSize(true)
            val requestManager = Glide.with(this)
            readerAdapter = ReaderAdapter(requestManager, readerViewModel, maxTileSize, this@VerticalReader)
            adapter = readerAdapter
            callBack = this@VerticalReader
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

            scrollToPosition(readerViewModel.currentPage.value)
        }
    }

    private fun bindViewModel() {
        readerViewModel.uiState.observe(viewLifecycleOwner) {
            onChapterContentChange(it)
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

    override fun onSingleTap(): Boolean {
        readerViewModel.toggleControllerVisibility()
        return true
    }

    override fun onReload() {
        readerViewModel.loadPages()
    }

    companion object {
        fun newInstance(): VerticalReader {
            return VerticalReader()
        }
    }
}