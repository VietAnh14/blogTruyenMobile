package com.vianh.blogtruyen.ui.reader.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.FutureTarget
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vianh.blogtruyen.databinding.LoadingPageItemBinding
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.ui.base.BaseViewHolder
import com.vianh.blogtruyen.ui.list.ListItem
import com.vianh.blogtruyen.ui.reader.ReaderViewModel
import com.vianh.blogtruyen.utils.SubsamplingScaleImageViewTarget
import timber.log.Timber
import java.io.File

class ReaderAdapter(
    val requestManager: RequestManager,
    val viewModel: ReaderViewModel,
    val tileSize: Int
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val pages = mutableListOf<ListItem>()
    var loadFutures: MutableList<FutureTarget<File>> = mutableListOf()

    override fun getItemCount(): Int {
        return pages.size
    }

    fun setPages(pages: List<ListItem>) {
        cancelImagePreloads()
        this.pages.clear()
        this.pages.addAll(pages)

        // Preload images
        for (page in pages) {
            if (page is PageItem) {
                val requestFuture = requestManager
                    .asFile()
                    .load(page.uri)
                    .submit()
                loadFutures.add(requestFuture)
            }
        }
        notifyDataSetChanged()
    }

    private fun cancelImagePreloads() {
        loadFutures.forEach {
            requestManager.clear(it)
        }
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