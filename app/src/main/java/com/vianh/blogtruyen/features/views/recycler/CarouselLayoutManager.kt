package com.vianh.blogtruyen.features.views.recycler

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

class CarouselLayoutManager(context: Context): LinearLayoutManager(context) {
    private val mShrinkAmount = 0.1f
    private val mShrinkDistance = 0.95f

    init {
        orientation = HORIZONTAL
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val scrolled = super.scrollHorizontallyBy(dx, recycler, state)

        val midpoint = width / 2f
        val d1 = mShrinkDistance * midpoint
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: return 0
            val childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2f
            val d = min(d1, abs(midpoint - childMidpoint))
            val scale = 1f - mShrinkAmount * d / d1
            child.scaleX = scale
            child.scaleY = scale
        }
        return scrolled

    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        lp?.width = (width * 0.8).toInt()
        return super.checkLayoutParams(lp)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {

        super.onLayoutChildren(recycler, state)

        scrollHorizontallyBy(0, recycler, state)
    }
}