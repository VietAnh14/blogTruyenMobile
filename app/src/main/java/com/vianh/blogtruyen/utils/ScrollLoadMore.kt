package com.vianh.blogtruyen.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScrollLoadMore(
    private val threshHold: Int = 0,
    private val loadFunc: () -> Unit
) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
        val totalItems = layoutManager.itemCount
        val firstPos = layoutManager.findFirstVisibleItemPosition()
        val visibleItemCount = recyclerView.childCount

        if (totalItems <= (firstPos + 1 + visibleItemCount + threshHold)) {
            loadFunc()
        }
    }
}