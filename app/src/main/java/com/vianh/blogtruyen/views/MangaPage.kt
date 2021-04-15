package com.vianh.blogtruyen.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class MangaPage @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attributeSet) {


    override fun onTouchEvent(event: MotionEvent): Boolean {
//        return super.onTouchEvent(event)
        return false
    }
}