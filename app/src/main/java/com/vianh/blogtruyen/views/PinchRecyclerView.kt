package com.vianh.blogtruyen.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.recyclerview.widget.RecyclerView

// https://stackoverflow.com/questions/37772918/android-change-recycler-view-column-no-on-pinch-zoom

class PinchRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val mScaleDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector

    var callBack: ReaderCallBack? = null

    private var minOffsetX = 0.0f
    private var minOffsetY = 0.0f

    private var width = 0f
    private var height = 0f

    private var preScale = 0f

    private val contentMatrix = Matrix()

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
        return mScaleDetector.isInProgress || super.onTouchEvent(ev)
    }

    private val mValues = FloatArray(9)
    fun updateMatrixValues() {
        contentMatrix.getValues(mValues)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.apply {
            save()
            updateMatrixValues()
            concat(contentMatrix)
            super.dispatchDraw(canvas)
            restore()
        }
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {

            val newScale = (preScale * detector.scaleFactor).coerceIn(1f, 3f)
            minOffsetX = width - width * newScale
            minOffsetY = height - height * newScale
            preScale = newScale


            updateMatrixValues()
            val currentScale = mValues[Matrix.MSCALE_X]

            // newScale = currentScale * scale < MAX_SCALE => scale < MAX_SCALE / scale (same for min)
            val minScale = 1f/currentScale
            val maxScale = 3f/currentScale
            val factor = detector.scaleFactor.coerceIn(minScale, maxScale)
            contentMatrix.postScale(factor, factor, detector.focusX, detector.focusY)

            constrainsBoundAndInvalidate()
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

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            contentMatrix.postTranslate(-distanceX, -distanceY)
            constrainsBoundAndInvalidate()
            return true
        }
    }

    fun constrainsBoundAndInvalidate() {
        updateMatrixValues()
        val currentDx = mValues[Matrix.MTRANS_X]
        val currentDy = mValues[Matrix.MTRANS_Y]

        // offset to translate back in if current offset is out of bounds
        var dx: Float = 0f
        var dy: Float = 0f

        if (currentDx < minOffsetX) {
            dx = minOffsetX - currentDx
        } else if (currentDx > 0) {
            dx = -currentDx
        }

        if (currentDy < minOffsetY) {
            dy = minOffsetY - currentDy
        } else if (currentDy > 0) {
            dy = -currentDy
        }

        contentMatrix.postTranslate(dx, dy)
        postInvalidate()
    }

    interface ReaderCallBack {
        fun onSingleTap(): Boolean
    }
}