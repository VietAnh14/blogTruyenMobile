package com.vianh.blogtruyen.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import com.vianh.blogtruyen.R

class MangaGridItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {
    private val ratioWidth: Int
    private val ratioHeight: Int

    init {
        context.theme
            .obtainStyledAttributes(attrs, R.styleable.MangaGridItem, 0, 0)
            .apply {
                ratioHeight = getInt(R.styleable.MangaGridItem_ratio_height, 9)
                ratioWidth = getInt(R.styleable.MangaGridItem_ratio_width, 6)
            }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val originWidth = MeasureSpec.getSize(widthMeasureSpec)
        val height = (originWidth * ratioHeight.toFloat() / ratioWidth).toInt()
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }
}