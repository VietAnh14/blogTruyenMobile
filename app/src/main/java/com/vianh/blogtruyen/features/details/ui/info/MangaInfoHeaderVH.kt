package com.vianh.blogtruyen.features.details.ui.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.MangaInfoItemBinding

class MangaInfoHeaderVH(private val binding: MangaInfoItemBinding) : RecyclerView.ViewHolder(binding.root) {
    init {

    }

    fun onBind(manga: Manga) {
        with(binding) {
            summary.setText(manga.description)
            categoryGroup.removeAllViews()

            for (category in manga.categories) {
                categoryGroup.addView(createChip(category.name))
            }
        }
    }


    private fun createChip(name: String): Chip {
        val chip = Chip(itemView.context)
        chip.text = name
        return chip
    }

    companion object {
        fun newInstance(parent: ViewGroup): MangaInfoHeaderVH {
            val binding = MangaInfoItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MangaInfoHeaderVH(binding)
        }
    }
}