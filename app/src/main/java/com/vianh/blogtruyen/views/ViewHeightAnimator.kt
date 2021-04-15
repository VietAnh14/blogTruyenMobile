package com.vianh.blogtruyen.views

import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import com.vianh.blogtruyen.utils.gone
import com.vianh.blogtruyen.utils.visible

class ViewHeightAnimator(val view: View) {
    var height = 0

    init {
        if (view.height > 0) {
            height = view.height
        } else {
            view.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    height = view.height
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }


    fun hide(duration: Long = 250, interpolator: Interpolator = AccelerateDecelerateInterpolator()) {
        view.animate()
            .translationY(height.toFloat())
            .setInterpolator(interpolator)
            .withEndAction {
                view.gone()
            }
            .setDuration(duration)
            .start()
    }

    fun show(duration: Long = 250, interpolator: Interpolator = AccelerateDecelerateInterpolator()) {
        view.visible()
        view.animate()
            .translationY(0f)
            .setInterpolator(interpolator)
            .setDuration(duration)
            .start()
    }
}