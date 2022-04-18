package com.vianh.blogtruyen.views.recycler

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.utils.ext.getDeviceHeight

class PreCacheLayoutManager(val context: Context,
                            orientation: Int = RecyclerView.VERTICAL,
                            reverseLayout: Boolean = false,
                            var extraSpace: Int = getDeviceHeight(context)/2):
    LinearLayoutManager(context, orientation, reverseLayout) {

    init {
        isItemPrefetchEnabled = false
    }

    override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
        return extraSpace
    }

    //    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
//        extraLayoutSpace[0] = extraSpace
//        extraLayoutSpace[1] = extraSpace
//    }
}