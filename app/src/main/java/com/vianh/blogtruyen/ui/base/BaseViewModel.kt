package com.vianh.blogtruyen.ui.base

import androidx.lifecycle.ViewModel
import com.vianh.blogtruyen.data.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class BaseViewModel(val dataManager: DataManager): ViewModel() {
    val TAG = this::class.java.simpleName
    // SupervisorJob won't cancel all child coroutines when a coroutine throw an exception
    val jobs = SupervisorJob()
    val uiScope = CoroutineScope(Dispatchers.Main + jobs)
    val ioScope = CoroutineScope(Dispatchers.IO + jobs)

    override fun onCleared() {
        super.onCleared()
        jobs.cancel()
    }
}