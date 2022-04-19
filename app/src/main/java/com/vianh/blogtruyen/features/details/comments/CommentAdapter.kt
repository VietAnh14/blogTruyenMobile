package com.vianh.blogtruyen.features.details.comments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.databinding.CommentItemBinding

class CommentAdapter(): ListAdapter<Comment, CommentVH>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        val inflater = LayoutInflater.from(parent.context)
        return CommentVH(CommentItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onViewRecycled(holder: CommentVH) {
        holder.onRecycle()
        super.onViewRecycled(holder)
    }

    class DiffUtils: DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }

    }
}