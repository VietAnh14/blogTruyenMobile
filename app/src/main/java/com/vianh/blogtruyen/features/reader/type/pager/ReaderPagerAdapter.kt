package com.vianh.blogtruyen.features.reader.type.pager

import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.RequestManager
import com.vianh.blogtruyen.data.prefs.ReaderMode
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.commonVH.ErrorItemVH
import com.vianh.blogtruyen.features.base.list.commonVH.LoadingItemVH
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import com.vianh.blogtruyen.features.reader.list.TransitionPageVH
import com.vianh.blogtruyen.utils.ext.await
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// TODO: Maybe create base reader adapter class
class ReaderPagerAdapter(
    private val requestManager: RequestManager,
    private val viewModel: ReaderViewModel,
    private val callback: Callback
) : AbstractAdapter<ListItem, Unit>(Unit) {

    private var preloadJob: Job? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder<out ListItem, Unit> {
        return when (viewType) {
            ReaderItem.PAGE_ITEM -> PagerItemViewHolder(parent, requestManager)
            ReaderItem.TRANSITION_ITEM -> TransitionPageVH(parent, viewModel, ReaderMode.HORIZON)
            ListItem.LOADING_ITEM -> LoadingItemVH(parent)
            ListItem.ERROR_ITEM -> ErrorItemVH(parent, callback)
            else -> throw IllegalStateException()
        }
    }

    override fun submitList(list: List<ListItem>?) {
        if (list == null)
            return

        preloadJob?.cancel()
        // Preload images
        if (!viewModel.isOffline) {
            preloadJob = viewModel.viewModelScope.launch {
                for (page in list) {
                    if (page is ReaderItem.PageItem) {
                        requestManager.asFile().await(page.uri)
                    }
                }
            }
        }

        super.submitList(list)
    }

    interface Callback : ErrorItemVH.ErrorReloadClick
}