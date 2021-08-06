package com.vianh.blogtruyen.features.mangaDetails.comments

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Comment
import com.vianh.blogtruyen.databinding.CommentItemBinding
import com.vianh.blogtruyen.utils.loadNetWorkImage

class CommentVH(val binding: CommentItemBinding): RecyclerView.ViewHolder(binding.root) {
    var data: Comment? = null
    private val topCommentMargin = itemView.context.resources.getDimensionPixelSize(R.dimen.comment_margin)
    private val replyCommentMargin = itemView.context.resources.getDimensionPixelSize(R.dimen.reply_comment_margin)

    fun onBind(item: Comment) {
        data = item
        with(binding) {
            avatar.loadNetWorkImage(item.avatar)
            time.text = item.time
            comment.text = item.message
            username.text = item.userName
        }

        if (item.type == Comment.TOP) {
            itemView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = topCommentMargin
            }
        } else {
            itemView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = replyCommentMargin
            }
        }
    }
}