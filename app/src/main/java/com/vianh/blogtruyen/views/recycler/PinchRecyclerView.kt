package com.vianh.blogtruyen.views.recycler

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.OverScroller
import androidx.recyclerview.widget.RecyclerView

// Referred https://stackoverflow.com/questions/12479859/view-with-horizontal-and-vertical-pan-drag-and-pinch-zoom/38205219#38205219

class PinchRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    companion object {
        const val MAX_SCALE = 3f
        const val MIN_SCALE = 1f
    }


    private val mScaleDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector

    private var minOffsetX = 0.0f
    private var minOffsetY = 0.0f

    private var width = 0f
    private var height = 0f

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
        if (!overScroller.isFinished) {
            overScroller.forceFinished(true)
        }

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
            updateMatrixValues()

            val currentScale = mValues[Matrix.MSCALE_X]
            val newScale = (currentScale * detector.scaleFactor).coerceIn(MIN_SCALE, MAX_SCALE)
            scale(newScale, detector.focusX, detector.focusY)
            return true
        }
    }

    private inner class GestureListener: GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            e ?: return false

            updateMatrixValues()
            val currentScale = mValues[Matrix.MSCALE_X]
            val newScale = if (currentScale > MIN_SCALE) 1f else MAX_SCALE
            startScaleAnimation(currentScale, newScale, e.x, e.y)
            return true
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

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            startFling(velocityX.toInt(), velocityY.toInt())
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    fun startScaleAnimation(from: Float, to: Float, focusX: Float, focusY: Float, duration: Long = 200) {
        val scaleAnimator = ValueAnimator
            .ofFloat(from, to)
            .setDuration(duration)
        scaleAnimator.interpolator = AccelerateDecelerateInterpolator()

        scaleAnimator.addUpdateListener {
            val scale = it.animatedValue as Float
            scale(scale, focusX, focusY)
        }
        scaleAnimator.start()
    }

    fun scale(scale: Float, focusX: Float, focusY: Float) {
        updateMatrixValues()

        val currentScale = mValues[Matrix.MSCALE_X]
        val newScale = scale.coerceIn(MIN_SCALE, MAX_SCALE)
        minOffsetX = width - width * newScale
        minOffsetY = height - height * newScale

        val factor = scale/currentScale
        transformsMatrix.postScale(factor, factor, focusX, focusY)
        constrainsBoundAndInvalidate()
    }

    val overScroller = OverScroller(context)
    fun startFling(velocityX: Int, velocityY: Int) {
        updateMatrixValues()
        if (mValues[Matrix.MSCALE_X] == MIN_SCALE) return

        val startX = mValues[Matrix.MTRANS_X].toInt()
        val startY = mValues[Matrix.MTRANS_Y].toInt()

        overScroller.forceFinished(true)
        overScroller.fling(startX, startY, velocityX, velocityY, minOffsetX.toInt(), 0, minOffsetY.toInt(), 0)
        postOnAnimation(object: Runnable {
            override fun run() {
                if (overScroller.computeScrollOffset()) {
                    updateMatrixValues()
                    val newX: Int = overScroller.currX
                    val newY: Int = overScroller.currY
                    val curX = mValues[Matrix.MTRANS_X].toInt()
                    val curY = mValues[Matrix.MTRANS_Y].toInt()
                    val transX: Int = newX - curX
                    val transY: Int = newY - curY
                    transformsMatrix.postTranslate(transX.toFloat(), transY.toFloat())
                    constrainsBoundAndInvalidate()
                    postOnAnimation(this)
                }
            }
        })
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