package com.vianh.blogtruyen.views

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class TouchGestureRecyclerView @JvmOverloads constructor (context: Context, attr: AttributeSet?, styles: Int = 0):
    RecyclerView(context, attr, styles) {
    var gestureDetector: GestureDetector? = null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // Todo: Override subsampling view, not consume the up event and handle touch here at on touch
        gestureDetector?.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
}