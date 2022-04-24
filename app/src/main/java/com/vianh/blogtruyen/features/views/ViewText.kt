package com.vianh.blogtruyen.features.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ViewMoreText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): AppCompatTextView(context, attrs, defStyleAttr)  {
    var content: String? = null
        private set

    fun setContent(text: String?) {
        content = text
        setText(content)
    }



}