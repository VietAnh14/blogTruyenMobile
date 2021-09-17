package com.vianh.blogtruyen.features.base.list

import android.view.View

interface ItemClick<T> {
    fun onClick(view: View, item: T)
    fun onLongClick(view: View, item: T): Boolean
}