package com.vianh.blogtruyen.features.list.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.databinding.FilterItemBinding


class FilterAdapter: ListAdapter<FilterCategoryItem, FilterAdapter.FilterItemVH>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterItemVH {
        val binding = FilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterItemVH(binding)
    }

    override fun onBindViewHolder(holder: FilterItemVH, position: Int) {
        holder.bind(getItem(position))
    }

    fun clearFilters() {
        currentList.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }


    class DiffCallback: DiffUtil.ItemCallback<FilterCategoryItem>() {
        override fun areItemsTheSame(
            oldItem: FilterCategoryItem,
            newItem: FilterCategoryItem
        ): Boolean {
            return newItem.category.name == oldItem.category.name
        }

        override fun areContentsTheSame(
            oldItem: FilterCategoryItem,
            newItem: FilterCategoryItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    inner class FilterItemVH(val binding: FilterItemBinding): RecyclerView.ViewHolder(binding.root) {

        init {

            fun onItemClick() {
                val item = getItem(bindingAdapterPosition)
                item?.let {
                    val isSelected = !it.isSelected
                    it.isSelected = isSelected
                    binding.checkBox.isChecked = isSelected
                }
            }

            itemView.setOnClickListener {
                onItemClick()
            }

            binding.checkBox.setOnClickListener {
                onItemClick()
            }
        }


        fun bind(item: FilterCategoryItem) {
            binding.categoryName.text = item.category.name
            binding.checkBox.isChecked = item.isSelected
        }
    }

}