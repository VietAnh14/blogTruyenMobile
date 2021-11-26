package com.vianh.blogtruyen.features.base.list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.vianh.blogtruyen.features.base.list.items.HasUniqueId
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.utils.inflate
import java.util.*

// T: List data
// E: Extra config
@Suppress("UNCHECKED_CAST")
abstract class AbstractViewHolder<T : ListItem, E>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var boundData: T? = null
    val context: Context
        get() = itemView.context

    constructor(@LayoutRes layoutRes: Int, parent: ViewGroup) : this(parent.inflate(layoutRes))

    fun requireData(): T = checkNotNull(boundData)

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

abstract class AbstractBindingHolder<T : ListItem, E, B : ViewBinding>(
    @LayoutRes layoutRes: Int,
    parent: ViewGroup
) : AbstractViewHolder<T, E>(layoutRes, parent) {
    val binding: B by lazy { bindToView(itemView) }

    protected abstract fun bindToView(view: View): B
}


abstract class AbstractAdapter<T : ListItem, E>(
    val extra: E,
    diffCallBack: DiffUtil.ItemCallback<T> = DiffCallBack()
) : ListAdapter<T, AbstractViewHolder<out T, E>>(diffCallBack) {

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

    class DiffCallBack<T : ListItem> : DiffUtil.ItemCallback<T>() {
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