package com.vianh.blogtruyen.features.base

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.vianh.blogtruyen.databinding.HistoryItemBinding
import com.vianh.blogtruyen.features.list.ListItem
import java.util.*
import kotlin.reflect.KFunction1


interface HasUniqueId<T> {
    val id: T
}

// T: List data
// E: Extra config
@Suppress("UNCHECKED_CAST")
abstract class AbstractViewHolder<T: ListItem, E>(itemView: View): RecyclerView.ViewHolder(itemView) {
    var boundData: T? = null

    fun bindData(data: ListItem, extra: E) {
        val itemData = data as T
        boundData = itemData
        onBind(itemData, extra)
    }

    abstract fun onBind(data: T, extra: E)

    open fun onRecycle() = Unit

    open fun onAttachToWindow() = Unit

    open fun onDetachFromWindow() = Unit
}


abstract class AbstractAdapter<T: ListItem, E>(val extra: E): ListAdapter<T, AbstractViewHolder<out T, E>>(DiffCallBack<T>()) {

    override fun onBindViewHolder(holder: AbstractViewHolder<out T, E>, position: Int) {
        holder.bindData(getItem(position), extra)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onViewRecycled(holder: AbstractViewHolder<out T, E>) {
        holder.onRecycle()
        super.onViewRecycled(holder)
    }

    override fun onViewAttachedToWindow(holder: AbstractViewHolder<out T, E>) {
        super.onViewAttachedToWindow(holder)
        holder.onAttachToWindow()
    }

    override fun onViewDetachedFromWindow(holder: AbstractViewHolder<out T, E>) {
        holder.onDetachFromWindow()
        super.onViewDetachedFromWindow(holder)
    }

    class DiffCallBack<T: ListItem>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            if (oldItem::class.java == newItem::class.java && oldItem is HasUniqueId<*> && newItem is HasUniqueId<*>)
                return oldItem.id == newItem.id
            else
                return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return Objects.equals(oldItem, newItem)
        }

    }
}