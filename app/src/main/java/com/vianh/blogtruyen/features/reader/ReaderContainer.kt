package com.vianh.blogtruyen.features.reader

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout

// Reader container to steal touch event from child views
class ReaderContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val gestureDetector = GestureDetector(context, GestureListener())
    var callback: Callback? = null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev)
    }

    inner class GestureListener: GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            return callback?.onSingleTap(e) ?: false
        }
    }

    interface Callback {
        fun onSingleTap(e: MotionEvent?): Boolean
    }
}