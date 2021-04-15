package com.vianh.blogtruyen.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

// https://stackoverflow.com/questions/37772918/android-change-recycler-view-column-no-on-pinch-zoom

class PinchRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var mActivePointerId = INVALID_POINTER_ID

    private lateinit var mScaleDetector: ScaleGestureDetector
    private var mScaleFactor = 1f
    private var scaleFocusX = 0f
    private var scaleFocusY = 0f

    private var maxWidth = 0.0f
    private var maxHeight = 0.0f

    private var mLastTouchX = 0f
    private var mLastTouchY = 0f

    private var mPosX = 0f
    private var mPosY = 0f

    private var width = 0f
    private var height = 0f

    init {
        if (!isInEditMode) mScaleDetector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        super.onTouchEvent(ev)
        mScaleDetector.onTouchEvent(ev)

        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val index = ev.actionIndex
                mLastTouchX = ev.getX(index)
                mLastTouchY = ev.getY(index)
                mActivePointerId = ev.getPointerId(0)
            }
            MotionEvent.ACTION_MOVE -> {

                /* this line is replaced because here came below isssue
                java.lang.IllegalArgumentException: pointerIndex out of range
                 ref http://stackoverflow.com/questions/6919292/pointerindex-out-of-range-android-multitouch
                */
                //final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                val pointerIndex = (action and MotionEvent.ACTION_POINTER_INDEX_MASK
                        shr MotionEvent.ACTION_POINTER_INDEX_SHIFT)
                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY
                mPosX += dx
                mPosY += dy
                if (mPosX > 0.0f) mPosX = 0.0f else if (mPosX < maxWidth) mPosX = maxWidth
                if (mPosY > 0.0f) mPosY = 0.0f else if (mPosY < maxHeight) mPosY = maxHeight
                mLastTouchX = x
                mLastTouchY = y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex =
                    action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastTouchX = ev.getX(newPointerIndex)
                    mLastTouchY = ev.getY(newPointerIndex)
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            save()
            translate(mPosX, mPosY)
            scale(mScaleFactor, mScaleFactor)
            restore()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.apply {
            save()
            if (mScaleFactor == 1.0f) {
                mPosX = 0.0f
                mPosY = 0.0f
            }
            translate(mPosX, mPosY)
            scale(mScaleFactor, mScaleFactor)
            super.dispatchDraw(canvas)
            restore()
            invalidate()
        }
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = max(1.0f, min(mScaleFactor, 3.0f))
            scaleFocusX = detector.focusX
            scaleFocusY = detector.focusY
            maxWidth = width - width * mScaleFactor
            maxHeight = height - height * mScaleFactor
            invalidate()
            return true
        }
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }
}