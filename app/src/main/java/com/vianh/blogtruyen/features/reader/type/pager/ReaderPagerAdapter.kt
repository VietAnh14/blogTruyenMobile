package com.vianh.blogtruyen.features.reader.type.pager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.RequestManager
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.commonVH.LoadingItemVH
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.base.list.items.LoadingItem
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import com.vianh.blogtruyen.features.reader.list.ReaderItem
import com.vianh.blogtruyen.features.reader.type.vertical.TransitionPageVH
import com.vianh.blogtruyen.utils.await
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReaderPagerAdapter(private val requestManager: RequestManager, private val viewModel: ReaderViewModel): AbstractAdapter<ListItem, Unit>(Unit) {

    private var preloadJob: Job? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<out ListItem, Unit> {
        return when(viewType) {
            ReaderItem.PAGE_ITEM -> PagerItemViewHolder(parent, requestManager)
            ReaderItem.TRANSITION_ITEM -> TransitionPageVH(TransitionPageBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewModel)
            ListItem.LOADING_ITEM -> LoadingItemVH(parent)
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
}