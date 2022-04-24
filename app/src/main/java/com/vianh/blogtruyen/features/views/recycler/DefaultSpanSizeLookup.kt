package com.vianh.blogtruyen.features.views.recycler

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DefaultSpanSizeLookup(private val parent: RecyclerView): GridLayoutManager.SpanSizeLookup() {
    private val normalItemViewTypes = HashSet<Int>()

    fun addViewType(viewType: Int) {
        normalItemViewTypes.add(viewType)
    }

    @SuppressLint("LogNotTimber")
    fun attachToParent() {
        val layoutManager = (parent.layoutManager as? GridLayoutManager)
        if (layoutManager != null) {
            layoutManager.spanSizeLookup = this
        } else {
            Log.w("GridSpanLookUp", "Can't attach to layout manager")
        }
    }

    override fun getSpanSize(position: Int): Int {
        val totalSpan = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
        val viewType = parent.adapter?.getItemViewType(position) ?: RecyclerView.NO_POSITION

        return if (normalItemViewTypes.contains(viewType)) {
            1
        } else {
            totalSpan
        }
    }

}