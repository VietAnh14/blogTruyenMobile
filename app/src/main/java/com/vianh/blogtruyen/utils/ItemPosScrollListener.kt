package com.vianh.blogtruyen.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemPosScrollListener(val onScrollToPos: (pos: Int) -> Unit): RecyclerView.OnScrollListener() {
    private var lastPos = -1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
        val centerView = recyclerView.findChildViewUnder(recyclerView.height/2f, recyclerView.width/2f)
        val pos = layoutManager.getPosition(centerView ?: return)
        if (lastPos != pos) {
            onScrollToPos.invoke(pos)
            lastPos = pos
        }
    }
}