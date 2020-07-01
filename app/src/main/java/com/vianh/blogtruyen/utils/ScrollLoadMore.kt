package com.vianh.blogtruyen.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScrollLoadMore(val threshHold: Int = 0, val loadFunc: () -> Unit):
    RecyclerView.OnScrollListener() {

    var isLoading = false
    var lastSize = 0
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {
            val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
            val totalItems = layoutManager.itemCount
            val firstPos = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = recyclerView.childCount
            if (lastSize < totalItems) {
                isLoading = false
            }
            if (!isLoading && totalItems - 1 <= (firstPos + visibleItemCount + threshHold)) {
                isLoading = true
                lastSize = totalItems
                loadFunc()
            }
        }
    }
}