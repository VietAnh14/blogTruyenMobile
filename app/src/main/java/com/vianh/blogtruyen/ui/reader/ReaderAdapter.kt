package com.vianh.blogtruyen.ui.reader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.vianh.blogtruyen.databinding.MangaPageItemBinding
import com.vianh.blogtruyen.databinding.PageItemBinding
import com.vianh.blogtruyen.databinding.TransitionPageBinding
import com.vianh.blogtruyen.ui.list.BaseVH
import com.vianh.blogtruyen.ui.list.ListItem
import java.lang.IllegalArgumentException

class ReaderAdapter(val requestManager: RequestManager, val tileSize: Int): RecyclerView.Adapter<BaseVH<*>>() {
    val pages = mutableListOf<ListItem>()

    override fun getItemCount(): Int {
        return pages.size
    }

    fun setPages(pages: List<ListItem>) {
        this.pages.clear()
        this.pages.addAll(pages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ListItem.PAGE_ITEM -> PageItemVH(requestManager, MangaPageItemBinding.inflate(inflater, parent, false), tileSize)
            ListItem.TRANSITION_ITEM -> TransitionPageVH(TransitionPageBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseVH<*>, position: Int) {
        holder.onBind(pages.get(position))
    }

    override fun getItemViewType(position: Int): Int {
        return pages[position].viewType
    }
}