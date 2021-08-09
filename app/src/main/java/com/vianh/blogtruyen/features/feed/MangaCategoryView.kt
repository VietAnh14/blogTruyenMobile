package com.vianh.blogtruyen.features.feed

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.vianh.blogtruyen.databinding.CategoryViewBinding

class MangaCategoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

//    private val adapter = NewFeedAdapter()
    private val binding = CategoryViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initRecycler()

    }


    private fun initRecycler() {
        with(binding.contentRecycler) {
//            adapter = adapter
//            setHasFixedSize(true)
            layoutManager = createLayoutManager()
        }
    }

    private fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

//    fun setItem (items: List<NewFeedItem>) {
//        adapter.submitList(items)
//    }
}