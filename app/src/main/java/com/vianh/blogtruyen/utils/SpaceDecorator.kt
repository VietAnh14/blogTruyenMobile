package com.vianh.blogtruyen.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceDecorator(val spacing: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val pos = parent.getChildAdapterPosition(view)
        if (pos != 0) {
            outRect.top = spacing
        }
    }
}