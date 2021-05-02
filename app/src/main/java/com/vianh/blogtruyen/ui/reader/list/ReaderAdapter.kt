package com.vianh.blogtruyen.ui.reader.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.databinding.LoadingPageItemBinding
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.ui.base.BaseViewHolder
import com.vianh.blogtruyen.ui.list.ListItem
import com.vianh.blogtruyen.ui.reader.ReaderViewModel
import com.vianh.blogtruyen.utils.preloadSuspend
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReaderAdapter(
    private val requestManager: RequestManager,
    private val viewModel: ReaderViewModel,
    private val tileSize: Int
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val pages = mutableListOf<ListItem>()
    private var preloadJob: Job? = null

    override fun getItemCount(): Int {
        return pages.size
    }

    fun setPages(pages: List<ListItem>) {
        cancelImagePreloads()
        this.pages.clear()
        this.pages.addAll(pages)

        // Preload images
        preloadJob = viewModel.viewModelScope.launch {
            for (page in pages) {
                if (page is PageItem) {
                    requestManager.preloadSuspend(page.uri)
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun cancelImagePreloads() {
        preloadJob?.cancel()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
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

            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        holder.onRecycle()
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(pages[position])
    }

    override fun getItemViewType(position: Int): Int {
        return pages[position].viewType
    }
}