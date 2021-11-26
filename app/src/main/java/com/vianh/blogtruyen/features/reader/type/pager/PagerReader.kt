package com.vianh.blogtruyen.features.reader.type.pager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.features.reader.Reader
import timber.log.Timber

@SuppressLint("ClickableViewAccessibility")
class PagerReader: Reader(R.layout.pager_reader_layout), ReaderPagerAdapter.Callback {

    private var pager: ViewPager2? = null
    private var adapter: ReaderPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager = view.findViewById(R.id.pager)

        setup()
        bindViewModel()
    }

    private fun setup() {
        adapter = ReaderPagerAdapter(Glide.with(requireContext()), readerViewModel, this)
        pager?.offscreenPageLimit = 3
        pager?.adapter = adapter
        pager?.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                readerViewModel.currentPage.value = position
                if (position == adapter?.itemCount?.minus(1)) {
                    readerViewModel.controllerVisibility.value = true
                }
            }
        })
    }

    private fun bindViewModel() {
        readerViewModel.uiState.observe(viewLifecycleOwner) {
            adapter?.submitList(it.items)
            toPage(readerViewModel.currentPage.value, false)
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

    override fun onReload() {
        readerViewModel.loadPages()
    }
}