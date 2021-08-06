package com.vianh.blogtruyen.features.reader

import com.bumptech.glide.RequestManager
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PageLoader(
    val isOffline: Boolean = false,
    private val requestManager: RequestManager,
    private val scope: CoroutineScope
) {
    private var loadingJob: Job? = null

    fun startLoad(items: List<ReaderItem>) {
        loadingJob = scope.launch {
            items.filterIsInstance<ReaderItem.PageItem>()
                .forEach {

                }
        }
    }

    fun cancel() {
        loadingJob?.cancel()
    }
}