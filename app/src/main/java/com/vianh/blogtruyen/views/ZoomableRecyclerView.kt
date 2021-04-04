package com.vianh.blogtruyen.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.recyclerview.widget.RecyclerView

class ZoomableRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attributeSet, defStyleAttr) {

    val scaleGestureListener = object: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            return super.onScale(detector)
        }
    }
    val gestureDetector = ScaleGestureDetector(context, scaleGestureListener)

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(e)
        return super.onTouchEvent(e)
    }
}