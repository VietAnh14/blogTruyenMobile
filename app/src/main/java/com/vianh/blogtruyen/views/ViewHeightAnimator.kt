package com.vianh.blogtruyen.views

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.view.doOnLayout
import com.vianh.blogtruyen.utils.ext.gone
import com.vianh.blogtruyen.utils.ext.visible

class ViewHeightAnimator(val view: View) {
    var height = 0

    init {
        if (view.height > 0) {
            height = view.height
        } else {
            view.doOnLayout {
                height = it.height
            }
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