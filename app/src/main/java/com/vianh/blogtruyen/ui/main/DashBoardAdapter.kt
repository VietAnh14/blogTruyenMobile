package com.vianh.blogtruyen.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.MangaItemBinding
import com.vianh.blogtruyen.ui.base.BaseBindingViewHolder

class DashBoardAdapter(private var items: MutableList<Manga>, val viewModel: MainViewModel):
    RecyclerView.Adapter<DashBoardAdapter.DashBoardViewHolder>() {

    fun getItems() = items
    fun setItems(items: MutableList<Manga>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<Manga>) {
        val index = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(index, items.size)
    }

    inner class DashBoardViewHolder(binding: MangaItemBinding) :
        BaseBindingViewHolder<MangaItemBinding>(binding) {
        override fun onBind(position: Int) {
            getBinding().manga = items[position]
            getBinding().viewModel = viewModel
            getBinding().executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MangaItemBinding.inflate(layoutInflater, parent, false)
        return DashBoardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DashBoardViewHolder, position: Int) {
        holder.onBind(position)
    }
}