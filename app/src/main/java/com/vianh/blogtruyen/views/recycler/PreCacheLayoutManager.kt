package com.vianh.blogtruyen.views.recycler

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.utils.ext.screenHeight

class PreCacheLayoutManager(
    val context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    var extraSpace: Int = context.screenHeight / 2
) : LinearLayoutManager(context, orientation, reverseLayout) {

    init {
        isItemPrefetchEnabled = false
    }

    override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
        return extraSpace
    }
}
