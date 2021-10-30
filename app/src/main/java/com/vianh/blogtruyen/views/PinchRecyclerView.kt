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
import timber.log.Timber

// Referred https://stackoverflow.com/questions/12479859/view-with-horizontal-and-vertical-pan-drag-and-pinch-zoom/38205219#38205219

class PinchRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val mScaleDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector

    private var minOffsetX = 0.0f
    private var minOffsetY = 0.0f

    private var width = 0f
    private var height = 0f

    private var preScale = 0f

    private val transformsMatrix = Matrix()
    private val invertMatrix = Matrix()

    init {
        gestureDetector = GestureDetector(context, GestureListener())
        mScaleDetector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.transform(invertMatrix)
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Transform to scaled event for detector to work properly
        ev.transform(transformsMatrix)
        gestureDetector.onTouchEvent(ev)
        mScaleDetector.onTouchEvent(ev)

        // Transform back to normal event
        ev.transform(invertMatrix)
        return super.onTouchEvent(ev)
    }

    private val mValues = FloatArray(9)
    fun updateMatrixValues() {
        transformsMatrix.getValues(mValues)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.apply {
            save()
            concat(transformsMatrix)
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

            // newScale = currentScale * scale < MAX_SCALE => scale < MAX_SCALE / currentScale (same for min)
            val minScale = 1f/currentScale
            val maxScale = 3f/currentScale
            val factor = detector.scaleFactor.coerceIn(minScale, maxScale)
            transformsMatrix.postScale(factor, factor, detector.focusX, detector.focusY)
            constrainsBoundAndInvalidate()
            return true
        }
    }

    private inner class GestureListener: GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return super.onDoubleTap(e)
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            transformsMatrix.postTranslate(-distanceX, -distanceY)
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

        transformsMatrix.postTranslate(dx, dy)
        transformsMatrix.invert(invertMatrix)
        postInvalidate()
    }
}