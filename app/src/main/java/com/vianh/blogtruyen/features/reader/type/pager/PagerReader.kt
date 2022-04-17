package com.vianh.blogtruyen.features.reader.type.pager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.reader.Reader
import com.vianh.blogtruyen.features.reader.list.TransitionPageVH
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

@SuppressLint("ClickableViewAccessibility")
class PagerReader: Reader(R.layout.pager_reader_layout), ReaderPagerAdapter.Callback {

    private var pager: ViewPager2? = null
    private var pagerAdapter: ReaderPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager = view.findViewById(R.id.pager)

        setup()
        bindViewModel()
    }

    private fun setup() {
        pagerAdapter = ReaderPagerAdapter(Glide.with(requireContext()), readerViewModel, this)
        pager?.apply {
            offscreenPageLimit = 3
            adapter = pagerAdapter
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    onPageChange(position)
                    if (position == adapter?.itemCount?.minus(1)) {
                        readerViewModel.controllerVisibility.value = true
                    }
                }
            })

            val pagerRecycler = this.getChildAt(0) as? RecyclerView
            if (pagerRecycler != null) {
                OverScrollDecoratorHelper
                    .setUpOverScroll(pagerRecycler, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
                    .setOverScrollUpdateListener { _, state, offset ->
                        if (offset <= 0) {
                            val pos = adapter?.itemCount ?: return@setOverScrollUpdateListener
                            val transitionVH = pagerRecycler.findViewHolderForAdapterPosition(pos - 1) as? TransitionPageVH
                            transitionVH?.onOverScroll(-offset.toInt(), state)
                        }
                    }
            }
        }
    }

    private fun bindViewModel() {
        readerViewModel.uiState.observe(viewLifecycleOwner) {
            pagerAdapter?.submitList(it.items)
            toPage(readerViewModel.currentPage.value, false)
        }
    }

    override fun onDestroyView() {
        pagerAdapter = null
        pager = null
        super.onDestroyView()
    }

    override fun toPage(pos: Int, animate: Boolean) {
        pager?.setCurrentItem(pos, animate)
    }

    companion object {
        fun newInstance(): PagerReader {
            return PagerReader()
        }
    }

    override fun onReload() {
        readerViewModel.loadPages()
    }
}