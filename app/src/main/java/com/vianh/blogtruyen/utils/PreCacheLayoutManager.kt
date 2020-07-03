package com.vianh.blogtruyen.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreCacheLayoutManager(val context: Context,
                            orientation: Int = RecyclerView.VERTICAL,
                            reverseLayout: Boolean = false,
                            val extraSpace: Int = getDeviceHeight(context)):
    LinearLayoutManager(context, orientation, reverseLayout) {

    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
        extraLayoutSpace[0] = extraSpace
        extraLayoutSpace[1] = extraSpace
    }
}