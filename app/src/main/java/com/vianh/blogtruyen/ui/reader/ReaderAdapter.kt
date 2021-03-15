package com.vianh.blogtruyen.ui.reader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.vianh.blogtruyen.databinding.MangaPageItemBinding

class ReaderAdapter(val requestManager: RequestManager): RecyclerView.Adapter<PageItemVH>() {
    val pages = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageItemVH {
        val inflater = LayoutInflater.from(parent.context)
        return PageItemVH(requestManager, MangaPageItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: PageItemVH, position: Int) {
        holder.onBind(pages[position])
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    fun setPages(pages: List<String>) {
        this.pages.clear()
        this.pages.addAll(pages)
        notifyDataSetChanged()
    }
}