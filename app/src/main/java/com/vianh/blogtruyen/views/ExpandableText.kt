package com.vianh.blogtruyen.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import com.vianh.blogtruyen.utils.gone
import com.vianh.blogtruyen.utils.visible
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.ExpandableTextViewBinding

class ExpandableText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {
    val binding = ExpandableTextViewBinding.inflate(LayoutInflater.from(context), this)
    private var maxLine = 3

    private var isExpand = false
    private val canShrink
        get() = binding.content.lineCount > maxLine

    init {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableText)
            maxLine = array.getInt(R.styleable.ExpandableText_max_lines, 3)
            val text = array.getString(R.styleable.ExpandableText_text)
            binding.content.maxLines = maxLine
            binding.content.text = text
            array.recycle()
        }

        this.setOnClickListener {
            toggleExpand()
        }

        doOnLayout {
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

    fun shrink() {
        if (!canShrink) {
            binding.actionBtn.gone()
            return
        }

        isExpand = false
        binding.content.maxLines = maxLine
        binding.actionBtn.visible()
    }

    fun expand() {
        if (!canShrink) {
            binding.actionBtn.gone()
            return
        }

        isExpand = true
        binding.actionBtn.gone()
        binding.content.maxLines = Int.MAX_VALUE
    }

    fun setText(text: String) {
        binding.content.text = text
        syncState()
    }

    fun setMaxLine(maxLine: Int) {
        this.maxLine = maxLine
        binding.content.maxLines = maxLine
        syncState()
    }
}