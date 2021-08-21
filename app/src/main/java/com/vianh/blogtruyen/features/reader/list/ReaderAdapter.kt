package com.vianh.blogtruyen.features.reader.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.databinding.ErrorReaderItemBinding
import com.vianh.blogtruyen.databinding.LoadingPageItemBinding
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.features.base.BaseViewHolder
import com.vianh.blogtruyen.features.base.list.AbstractAdapter
import com.vianh.blogtruyen.features.base.list.AbstractViewHolder
import com.vianh.blogtruyen.features.base.list.ListItem
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import com.vianh.blogtruyen.utils.await
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReaderAdapter(
    private val requestManager: RequestManager,
    private val viewModel: ReaderViewModel,
    private val tileSize: Int
) : AbstractAdapter<ReaderItem, Unit>(Unit) {

    private var preloadJob: Job? = null

    fun setPages(pages: List<ReaderItem>) {
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
    ): AbstractViewHolder<out ReaderItem, Unit> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ListItem.PAGE_ITEM -> {
                val binding = MangaPageItemBinding.inflate(inflater, parent, false)
                binding.page.setMaxTileSize(tileSize)
                binding.page.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                binding.page.setMinimumDpi(90)
                binding.page.setMinimumTileDpi(180)
                PageItemVH(binding, requestManager)
            }

            ListItem.TRANSITION_ITEM -> TransitionPageVH(
                TransitionPageBinding.inflate(
                    inflater,
                    parent,
                    false
                ), viewModel
            )

            ListItem.LOADING_ITEM -> LoadingItemVH(
                LoadingPageItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            ListItem.ERROR_TYPE -> ErrorVH(
                ErrorReaderItemBinding.inflate(inflater, parent, false),
                viewModel
            )

            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }
}