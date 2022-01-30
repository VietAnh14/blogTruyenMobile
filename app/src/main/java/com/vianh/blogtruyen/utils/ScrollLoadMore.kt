package com.vianh.blogtruyen.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScrollLoadMore(
    private val threshHold: Int = 0,
    private val loadFunc: () -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = (recyclerView.layoutManager as? LinearLayoutManager) ?: return
        val lastItemPos = layoutManager.findLastVisibleItemPosition()

        if (layoutManager.itemCount <= lastItemPos + 1 + threshHold) {
            loadFunc()
        }
    }
}