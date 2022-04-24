package com.vianh.blogtruyen.features.views.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridItemSpacingDecorator(val spacing: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val spanCount = (parent.layoutManager as GridLayoutManager).spanCount
        when (parent.getChildLayoutPosition(view) % spanCount) {
            0 -> {
                outRect.left = spacing
                outRect.right = spacing/2
            }
            spanCount - 1 -> {
                outRect.left = spacing/2
                outRect.right = spacing
            }
            else -> {
                outRect.left = spacing/2
                outRect.right = spacing/2
            }
        }
    }
}