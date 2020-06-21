package com.vianh.blogtruyen.ui.base

import androidx.lifecycle.ViewModel
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.PreviewManga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class BaseViewModel(val dataManager: DataManager): ViewModel() {
    val TAG = this::class.java.simpleName
    val jobs = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + jobs)
    val ioScope = CoroutineScope(Dispatchers.IO + jobs)

    override fun onCleared() {
        super.onCleared()
        jobs.cancel()
    }
}