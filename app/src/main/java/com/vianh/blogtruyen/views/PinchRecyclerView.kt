package com.vianh.blogtruyen.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import javax.security.auth.callback.Callback
import kotlin.math.max
import kotlin.math.min

// https://stackoverflow.com/questions/37772918/android-change-recycler-view-column-no-on-pinch-zoom

class PinchRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var mActivePointerId = INVALID_POINTER_ID

    private val mScaleDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector

    var callBack: ReaderCallBack? = null

    private var mScaleFactor = 1f

    private var maxOffsetX = 0.0f
    private var maxOffsetY = 0.0f

    private var mLastTouchX = 0f
    private var mLastTouchY = 0f

    private var mPosX = 0f
    private var mPosY = 0f

    private var width = 0f
    private var height = 0f

    init {
        gestureDetector = GestureDetector(context, GestureListener())
        mScaleDetector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(ev)
        gestureDetector.onTouchEvent(ev)

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
                if (mPosX > 0.0f) mPosX = 0.0f else if (mPosX < maxOffsetX) mPosX = maxOffsetX
                if (mPosY > 0.0f) mPosY = 0.0f else if (mPosY < maxOffsetY) mPosY = maxOffsetY
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
        return super.onTouchEvent(ev)
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
        private var scaleFocusBeginX = 0f
        private var scaleFocusBeginY = 0f

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            detector?.let {
                scaleFocusBeginX = it.focusX
                scaleFocusBeginY = it.focusY
            }
            return super.onScaleBegin(detector)
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = max(1.0f, min(mScaleFactor, 3.0f))
            maxOffsetX = width - width * mScaleFactor
            maxOffsetY = height - height * mScaleFactor

            val scaleDiffX = width/2 - mScaleFactor*detector.focusX
            val scaleDiffY = height/2 - mScaleFactor*detector.focusY
            mPosX = scaleDiffX
            mPosY = scaleDiffY
            invalidate()
            return true
        }
    }

    private inner class GestureListener: GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            return callBack?.onSingleTap() ?: false || super.onSingleTapConfirmed(e)
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return super.onDoubleTap(e)
        }
    }

    interface ReaderCallBack {
        fun onSingleTap(): Boolean
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }
}