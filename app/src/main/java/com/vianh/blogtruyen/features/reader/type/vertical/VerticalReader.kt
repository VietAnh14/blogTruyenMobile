package com.vianh.blogtruyen.features.reader.type.vertical

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.base.list.commonVH.ErrorItemVH
import com.vianh.blogtruyen.features.reader.Reader
import com.vianh.blogtruyen.features.reader.ReaderModel
import com.vianh.blogtruyen.features.reader.list.TransitionPageVH
import com.vianh.blogtruyen.utils.*
import com.vianh.blogtruyen.utils.ext.maxTileSize
import com.vianh.blogtruyen.views.recycler.ItemPosScrollListener
import com.vianh.blogtruyen.views.recycler.PreCacheLayoutManager
import com.vianh.blogtruyen.views.recycler.SpaceDecorator
import com.vianh.blogtruyen.views.recycler.PinchRecyclerView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class VerticalReader: Reader(R.layout.vertical_reader_layout), ErrorItemVH.ErrorReloadClick {

    private var readerAdapter: ReaderAdapter? = null
    private var contentRecycler: PinchRecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(view, savedInstanceState)
        bindViewModel()
    }

    private fun setup(view: View, savedInstanceState: Bundle?) {
        val addSpace = arguments?.getBoolean(ADD_SPACE_KEY) ?: true
        contentRecycler = view.findViewById<PinchRecyclerView>(R.id.reader_recycler)
        readerAdapter = ReaderAdapter(Glide.with(this), readerViewModel, maxTileSize, this)

        contentRecycler?.apply {
            setHasFixedSize(true)
            adapter = readerAdapter
            layoutManager = PreCacheLayoutManager(requireContext())

            OverScrollDecoratorHelper
                .setUpOverScroll(this, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
                .setOverScrollUpdateListener { _, state, offset ->
                    if (offset <= 0) {
                        val pos = adapter?.itemCount ?: return@setOverScrollUpdateListener
                        val transitionVH =
                            findViewHolderForAdapterPosition(pos - 1) as? TransitionPageVH
                        transitionVH?.onOverScroll((-offset).toInt(), state)
                    }
                }

            addOnScrollListener(ItemPosScrollListener { onPageChange(it) })
             if (addSpace) {
                 addItemDecoration(SpaceDecorator(requireContext().resources.getDimensionPixelSize(R.dimen.vertical_reader_space)))
             }
        }
    }

    private fun bindViewModel() {
        readerViewModel.uiState.observe(viewLifecycleOwner) {
            onChapterContentChange(it)
            contentRecycler?.scrollToPosition(readerViewModel.currentPage.value)
        }
    }

    private fun onChapterContentChange(model: ReaderModel) {
        readerAdapter?.setPages(model.items)
    }

    override fun toPage(pos: Int, animate: Boolean) {
        if (animate) {
            contentRecycler?.smoothScrollToPosition(pos)
        } else {
            contentRecycler?.scrollToPosition(pos)
        }
    }

    override fun onReload() {
        readerViewModel.loadPages()
    }

    override fun onDestroyView() {
        readerAdapter = null
        contentRecycler = null
        super.onDestroyView()
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