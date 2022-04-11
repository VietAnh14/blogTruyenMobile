package com.vianh.blogtruyen.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.google.android.material.elevation.SurfaceColors
import com.vianh.blogtruyen.R

inline fun ImageView.loadNetWorkImage(url: String, @DrawableRes errorDrawable: Int = R.drawable.ic_broken_image) {
    Glide.with(context)
        .load(url)
        .error(errorDrawable)
        .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
        .into(this)
}

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

fun View.toggleState(direction: Int) {
    if (visibility == View.GONE) {
        visibility = View.VISIBLE
        animate().setDuration(200).translationY(0f)
    } else {
        when (direction) {
            Gravity.TOP -> animate().setDuration(200).translationY(-height.toFloat())
                .withEndAction {
                    visibility = View.GONE
                }
            Gravity.BOTTOM -> animate().setDuration(200).translationY(height.toFloat())
                .withEndAction {
                    visibility = View.GONE
                }
        }
    }
}

fun View.slideDown(duration: Long = 400, interpolator: Interpolator = AccelerateDecelerateInterpolator()) {
    animate()
        .translationY(height.toFloat())
        .setInterpolator(interpolator)
        .setDuration(duration)
        .start()
}

fun View.slideUp(duration: Long = 400, interpolator: Interpolator = AccelerateDecelerateInterpolator()) {
    animate()
        .translationY(height * -1f)
        .setInterpolator(interpolator)
        .setDuration(duration)
        .start()
}

fun View.resetPos(duration: Long = 400, interpolator: Interpolator = AccelerateInterpolator()) {
    animate()
        .translationY(0f)
        .setInterpolator(interpolator)
        .setDuration(duration)
        .start()
}

fun View.showSoftKeyBoard() {
    val keyboard: InputMethodManager? = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    keyboard?.showSoftInput(this, 0)
}

fun View.hideSoftKeyboard() {
    val keyboard: InputMethodManager? = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    keyboard?.hideSoftInputFromWindow(windowToken, 0)
}

inline fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToParent: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToParent)
}

inline fun <T : ViewBinding> ViewGroup.viewBinding(factory: (inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> T) =
    factory(LayoutInflater.from(context), this, false)

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

inline fun Context.typeValue(resId: Int): TypedValue {
    val outValue = TypedValue()
    this.theme.resolveAttribute(resId, outValue, true)
    return outValue
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()

fun Number.dpToPx(context: Context): Float {
    return this.toFloat() * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun Number.pxToDp(context: Context): Float {
    return this.toFloat() / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.attrDimenPixel(id: Int): Int {
    return TypedValue.complexToDimensionPixelSize(typeValue(id).data, resources.displayMetrics)
}

fun Context.getSurfaceColorPrimary(): Int {
    return SurfaceColors.SURFACE_2.getColor(this)
}

val screenHeight
    get() = Resources.getSystem().displayMetrics.heightPixels

val screenWidth
    get() = Resources.getSystem().displayMetrics.widthPixels
