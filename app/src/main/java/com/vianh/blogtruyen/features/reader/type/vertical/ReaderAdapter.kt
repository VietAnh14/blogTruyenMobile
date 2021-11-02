package com.vianh.blogtruyen.features.reader.type.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.RequestManager
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.commonVH.ErrorItemVH
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.base.list.commonVH.LoadingItemVH
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import com.vianh.blogtruyen.utils.await
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReaderAdapter(
    private val requestManager: RequestManager,
    private val viewModel: ReaderViewModel,
    private val tileSize: Int,
    private val listener: ErrorItemVH.ErrorReloadClick
) : AbstractAdapter<ListItem, Unit>(Unit) {

    private var preloadJob: Job? = null

    fun setPages(pages: List<ListItem>) {
        cancelImagePreloads()

        // Preload images
        if (!viewModel.isOffline) {
            preloadJob = viewModel.viewModelScope.launch {
                for (page in pages) {
                    if (page is ReaderItem.PageItem) {
                        requestManager.asFile().await(page.uri)
                    }
                }
            }
        }
        submitList(pages)
    }

    private fun cancelImagePreloads() {
        preloadJob?.cancel()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder<out ListItem, Unit> {
        return when (viewType) {
            ReaderItem.PAGE_ITEM -> PageItemVH(parent, requestManager, tileSize)
            ReaderItem.TRANSITION_ITEM -> TransitionPageVH(parent, viewModel)
            ListItem.LOADING_ITEM -> LoadingItemVH(parent)
            ListItem.ERROR_ITEM -> ErrorItemVH(parent, listener)

            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }
}