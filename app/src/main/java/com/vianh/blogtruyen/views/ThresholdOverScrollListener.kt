package com.vianh.blogtruyen.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.utils.visible
import me.everything.android.ui.overscroll.IOverScrollDecor
import me.everything.android.ui.overscroll.IOverScrollUpdateListener
import kotlin.math.min

class ThresholdOverScrollListener(private val progressBar: View, val callBack: () -> Unit) :
    IOverScrollUpdateListener {
    var size = progressBar.context.resources.getDimensionPixelSize(R.dimen.progress_bar_height)
    var isLoading = false

//    init {
//        progressBar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                size = progressBar.height
//                progressBar.viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//
//        })
//    }

    override fun onOverScrollUpdate(decor: IOverScrollDecor?, state: Int, offset: Float) {
        if (offset >= 0 && !isLoading) {
            progressBar.visible()
            val faction = min(offset / 200, 1f)
            progressBar.layoutParams.height = (faction * size).toInt()
            progressBar.requestLayout()
            progressBar.translationY = 0f

            if (faction >= 1f) {
                isLoading = true
                callBack()
            }
        }
    }

    fun hideProgressBar() {
        ObjectAnimator.ofInt(size, 0).apply {
            interpolator = DecelerateInterpolator()
            duration = 150
            addUpdateListener {
                progressBar.layoutParams.height = animatedValue as Int
                progressBar.requestLayout()
                progressBar.translationY = 0f
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    isLoading = false
                }
            })
            start()
        }
    }

    fun onFinishRefresh() {
        isLoading = false
        hideProgressBar()
    }
}