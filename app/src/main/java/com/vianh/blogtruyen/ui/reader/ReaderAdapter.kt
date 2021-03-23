package com.vianh.blogtruyen.ui.reader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.Target
import com.vianh.blogtruyen.databinding.LoadingPageItemBinding
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.ui.base.BaseViewHolder
import com.vianh.blogtruyen.ui.list.ListItem
import timber.log.Timber

class ReaderAdapter(
    val requestManager: RequestManager,
    val viewModel: ReaderViewModel,
    val tileSize: Int
) : RecyclerView.Adapter<BaseViewHolder>(), ListPreloader.PreloadModelProvider<ListItem> {
    val pages = mutableListOf<ListItem>()

    override fun getItemCount(): Int {
        return pages.size
    }

    fun setPages(pages: List<ListItem>) {
        this.pages.clear()
        this.pages.addAll(pages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ListItem.PAGE_ITEM -> PageMannualCreateVH(
                PageMannualCreateVH.createView(
                    parent.context,
                    tileSize
                ), requestManager
            )
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

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(pages.get(position))
    }

    override fun getItemViewType(position: Int): Int {
        return pages[position].viewType
    }

    override fun getPreloadItems(position: Int): MutableList<ListItem> {
        Timber.d("Get preload items")
        return pages.subList(position, position + 1)
    }

    override fun getPreloadRequestBuilder(item: ListItem): RequestBuilder<*>? {
        val page = item as? PageItem
        Timber.d("Preload items $page")
        return if (page != null) {
            requestManager.download(page.uri).override(Target.SIZE_ORIGINAL)
        } else {
            null
        }
    }
}