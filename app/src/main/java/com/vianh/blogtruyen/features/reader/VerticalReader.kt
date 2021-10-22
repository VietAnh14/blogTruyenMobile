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
import com.vianh.blogtruyen.utils.getMaxTextureSize
import com.vianh.blogtruyen.views.PinchRecyclerView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import timber.log.Timber

class VerticalReader: Reader(R.layout.vertical_reader_layout), PinchRecyclerView.ReaderCallBack, ErrorItemVH.ErrorReloadClick {

    private val tileSize by lazy {
        getMaxTextureSize().also {
            Timber.d("Max textureSize $it")
        }
    }

    private var readerAdapter: ReaderAdapter? = null
    private var pinchRecyclerView: PinchRecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(view, savedInstanceState)
    }

    private fun setup(view: View, savedInstanceState: Bundle?) {
        pinchRecyclerView = view.findViewById<PinchRecyclerView>(R.id.reader_recycler)
        with(checkNotNull(pinchRecyclerView)) {
            setHasFixedSize(true)
            callBack = this@VerticalReader
            layoutManager = PreCacheLayoutManager(requireContext())
            val overScrollDecor = OverScrollDecoratorHelper
                .setUpOverScroll(this, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

            addOnScrollListener(ItemPosScrollListener {
                callback.onPageChange(it)
            })
            overScrollDecor.setOverScrollUpdateListener { _, state, offset ->
                if (offset <= 0) {
                    val pos = adapter?.itemCount ?: return@setOverScrollUpdateListener
                    val transitionVH =
                        findViewHolderForAdapterPosition(pos - 1) as? TransitionPageVH
                    transitionVH?.onOverScroll((-1 * offset).toInt(), state)
                }
            }

            val requestManager = Glide.with(this)
            readerAdapter = ReaderAdapter(requestManager, readerViewModel, tileSize, this@VerticalReader)
            adapter = readerAdapter
        }
    }

    override fun onContentChange(model: ReaderModel) {
        readerAdapter?.setPages(model.items)
        pinchRecyclerView?.scrollToPosition(0)
    }

    override fun toPage(pos: Int, animate: Boolean) {
        if (animate) {
            pinchRecyclerView?.smoothScrollToPosition(pos)
        } else {
            pinchRecyclerView?.scrollToPosition(pos)
        }
    }

    override fun onSingleTap(): Boolean {
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