package com.vianh.blogtruyen.features.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.ExpandableTextViewBinding
import com.vianh.blogtruyen.utils.ext.gone
import com.vianh.blogtruyen.utils.ext.visible

class ExpandableText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {
    val binding = ExpandableTextViewBinding.inflate(LayoutInflater.from(context), this)

    private var text: String? = null
    private var maxLine = 3
    private var isExpand = false
    private val contentText
        get() = binding.content

    private val canShrink
        get() = binding.content.lineCount > maxLine

    init {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableText)
            maxLine = array.getInt(R.styleable.ExpandableText_max_lines, 3)
            text = array.getString(R.styleable.ExpandableText_text)

            contentText.maxLines = maxLine
            contentText.text = text
            array.recycle()
        }

        this.setOnClickListener {
            toggleExpand()
        }

        // Wait for layout to get line count, doOnLayout lineCount return 0 so we use onPreDraw
        doOnPreDraw {
            syncState()
        }
    }

    fun syncState() {
        if (isExpand) {
            expand()
        } else {
            shrink()
        }
    }

    fun toggleExpand() {
        if (isExpand) {
            shrink()
        } else {
            expand()
        }
    }

    @SuppressLint("SetTextI18n")
    fun shrink() {
        if (!canShrink) {
            binding.actionBtn.gone()
            return
        }

        isExpand = false
        contentText.updatePadding(bottom = 0)
        contentText.maxLines = maxLine
        binding.actionBtn.text = "...${context.getString(R.string.view_more)}"
        binding.actionBtn.visible()
    }

    fun expand() {
        if (!canShrink) {
            binding.actionBtn.gone()
            return
        }

        isExpand = true
        binding.actionBtn.visible()
        binding.actionBtn.text = context.getString(R.string.hide)
        contentText.updatePadding(bottom = binding.content.lineHeight)
        contentText.maxLines = Int.MAX_VALUE
    }

    fun setText(text: String) {
        contentText.text = text
        syncState()
    }

    fun getText() = text

    fun setMaxLine(maxLine: Int) {
        this.maxLine = maxLine
        contentText.maxLines = maxLine
        syncState()
    }
}