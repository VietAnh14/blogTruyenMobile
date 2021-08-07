package com.vianh.blogtruyen.features.reader

import com.bumptech.glide.RequestManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PageLoader(
    val isOffline: Boolean = false,
    private val requestManager: RequestManager,
    private val scope: CoroutineScope
) {
    private var loadingJob: Job? = null

    fun enqueue(uri: String): StateFlow<String> {
        val result = MutableStateFlow("abc")
        loadingJob = scope.launch {

        }

        return result
    }

    fun cancel() {
        loadingJob?.cancel()
    }
}