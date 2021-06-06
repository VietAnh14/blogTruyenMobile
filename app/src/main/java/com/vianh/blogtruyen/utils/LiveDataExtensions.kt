package com.vianh.blogtruyen.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.StateFlow

fun <T> StateFlow<T>.observeAsLiveData(owner: LifecycleOwner, observer: Observer<T>) {
    this.asLiveData().observe(owner, observer)
}