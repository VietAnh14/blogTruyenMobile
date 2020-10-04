package com.vianh.blogtruyen.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class AutoColumnRecyclerview @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    private var columnWidth: Int = -1

    init {
        if (attributeSet != null) {
            val attrsArray = intArrayOf(android.R.attr.columnWidth)
            val array = context.obtainStyledAttributes(attributeSet, attrsArray)
            columnWidth = array.getDimensionPixelSize(0, -1)
            array.recycle()
        }
        layoutManager = GridLayoutManager(context, 1)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (columnWidth > 0) {
            val spanCount = max(1, measuredWidth/columnWidth)
            (layoutManager as GridLayoutManager).spanCount = spanCount
        }
    }
}