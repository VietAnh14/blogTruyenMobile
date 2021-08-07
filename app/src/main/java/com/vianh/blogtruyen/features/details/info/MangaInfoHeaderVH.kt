package com.vianh.blogtruyen.features.details.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.ChipItemBinding
import com.vianh.blogtruyen.databinding.MangaInfoItemBinding
import com.vianh.blogtruyen.features.details.MangaDetailsViewModel
import com.vianh.blogtruyen.utils.loadNetWorkImage

class MangaInfoHeaderVH(
    private val binding: MangaInfoItemBinding,
    private val viewModel: MangaDetailsViewModel
) : RecyclerView.ViewHolder(binding.root) {
    init {

    }

    fun onBind(manga: Manga) {
        with(binding) {
            headerCover.loadNetWorkImage(manga.imageUrl)
            smallCover.loadNetWorkImage(manga.imageUrl)
            mangaTitle.text = manga.title
            summary.text = manga.description
            categoryGroup.removeAllViews()

            for (category in manga.categories) {
                categoryGroup.addView(createChip(category.name))
            }
        }
    }


    private fun createChip(name: String): Chip {
        val chipBinding = ChipItemBinding.inflate(LayoutInflater.from(itemView.context))
        chipBinding.chip.text = name
        return chipBinding.chip
    }

    companion object {
        fun newInstance(parent: ViewGroup, viewModel: MangaDetailsViewModel): MangaInfoHeaderVH {
            val binding = MangaInfoItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MangaInfoHeaderVH(binding, viewModel)
        }
    }
}