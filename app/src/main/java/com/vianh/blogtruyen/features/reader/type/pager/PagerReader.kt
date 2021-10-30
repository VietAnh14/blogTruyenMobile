package com.vianh.blogtruyen.features.reader.type.pager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.reader.Reader
import timber.log.Timber

@SuppressLint("ClickableViewAccessibility")
class PagerReader: Reader(R.layout.pager_reader_layout) {

    private var pager: ViewPager2? = null
    private var adapter: ReaderPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager = view.findViewById(R.id.pager)

        setup()
        bindViewModel()
    }

    private fun setup() {
        adapter = ReaderPagerAdapter(Glide.with(requireContext()) ,readerViewModel)
        pager?.adapter = adapter
        toPage(readerViewModel.currentPage.value, false)
        pager?.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                readerViewModel.currentPage.value = position
            }
        })
    }

    private fun bindViewModel() {
        readerViewModel.uiState.observe(viewLifecycleOwner) {
            adapter?.submitList(it.items)
        }
    }

    override fun onDestroyView() {
        adapter = null
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
}