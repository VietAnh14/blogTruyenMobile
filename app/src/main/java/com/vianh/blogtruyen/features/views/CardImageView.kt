package com.vianh.blogtruyen.features.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.utils.ext.getThemeColor
import com.vianh.blogtruyen.utils.ext.gone
import com.vianh.blogtruyen.utils.ext.toPx

class CardImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr), RequestListener<Drawable> {
    private val ratioWidth: Int
    private val ratioHeight: Int
    private val image = AppCompatImageView(context)
    private val loadingIndicator = CircularProgressIndicator(context)
    private val glide by lazy { Glide.with(this) }

    init {
        context.theme
            .obtainStyledAttributes(attrs, R.styleable.MangaGridItem, 0, 0)
            .apply {
                ratioHeight = getInt(R.styleable.MangaGridItem_ratio_height, 9)
                ratioWidth = getInt(R.styleable.MangaGridItem_ratio_width, 6)
            }

        image.scaleType = ImageView.ScaleType.CENTER_CROP
        loadingIndicator.apply {
            trackCornerRadius = 10.toPx
            indicatorSize = 24.toPx
            trackThickness = 4.toPx
            setIndicatorColor(context.getThemeColor(R.attr.colorAccent))
            isIndeterminate = true
        }
        addView(image, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(loadingIndicator, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER))
    }

    fun loadImage(uri: String) {
        loadingIndicator.show()
        glide
            .load(uri)
            .listener(this)
            .error(R.drawable.ic_broken_image)
            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
            .into(image)
    }

    fun clearLoading() {
        glide.clear(image)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val originWidth = MeasureSpec.getSize(widthMeasureSpec)
        val height = (originWidth * ratioHeight.toFloat() / ratioWidth).toInt()
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        loadingIndicator.gone()
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        loadingIndicator.gone()
        return false
    }
}