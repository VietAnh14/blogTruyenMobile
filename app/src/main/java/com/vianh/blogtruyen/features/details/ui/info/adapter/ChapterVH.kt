package com.vianh.blogtruyen.features.details.ui.info.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.databinding.ChapterItemBinding
import com.vianh.blogtruyen.features.download.DownloadState
import com.vianh.blogtruyen.utils.ext.getThemeColor
import com.vianh.blogtruyen.utils.ext.typeValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class ChapterVH(
    val binding: ChapterItemBinding,
    clickListener: ChapterClick,
    private val scope: CoroutineScope
) : RecyclerView.ViewHolder(binding.root) {

    var data: ChapterItem? = null
    var downloadJob: Job? = null

    init {
        itemView.setOnClickListener {
            data?.let { clickListener.onChapterClick(it) }
        }

        itemView.setOnLongClickListener {
            data?.let { chapter ->
                clickListener.onChapterLongClick(chapter, it, bindingAdapterPosition)
            }
            true
        }

        binding.stateButton.setOnClickListener {
            onStateClick(clickListener)
        }
    }

    fun onBind(item: ChapterItem, selectedChapters: Set<Chapter>) {
        data = item

        if (selectedChapters.contains(item.chapter)) {
            itemView.setBackgroundColor(itemView.context.typeValue(R.attr.colorControlHighlight).data)
        } else {
            itemView.setBackgroundResource(itemView.context.typeValue(R.attr.selectableItemBackground).resourceId)
        }

        binding.chapterName.text = item.chapter.name
        binding.uploadDate.text = item.uploadTimeString
        val textColor = if (item.chapter.read) {
            ContextCompat.getColor(itemView.context, R.color.textColorDeactivated)
        } else {
            itemView.context.getThemeColor(R.attr.colorOnSurface)
        }

        binding.chapterName.setTextColor(textColor)

        downloadJob = item.downloadState.onEach {
            when (it) {
                is DownloadState.NotDownloaded -> {
                    binding.stateButton.setImageResource(R.drawable.ic_file_download)
                }

                is DownloadState.Downloaded -> {
                    binding.stateButton.setImageResource(R.drawable.ic_download_done)
                }

                is DownloadState.Queued -> {
                    binding.stateButton.setImageResource(R.drawable.ic_clear_all)
                }

                is DownloadState.Error -> {
                    binding.stateButton.setImageResource(R.drawable.ic_book_24)
                }

                is DownloadState.InProgress -> {
                    Timber.d("Download progress: ${it.progress}")
                    binding.stateButton.setImageResource(R.drawable.ic_downloading)
                }
            }
        }.launchIn(scope)
    }

    fun onRecycle() {
        downloadJob?.cancel()
    }

    fun onDetached() {
        downloadJob?.cancel()
    }

    private fun onStateClick(clickListener: ChapterClick) {
        if (data?.downloadState?.value !is DownloadState.NotDownloaded)
            return

        clickListener.onStateButtonClick(data ?: return)
    }

    interface ChapterClick {
        fun onChapterClick(chapter: ChapterItem)
        fun onChapterLongClick(chapter: ChapterItem, view: View, position: Int)
        fun onStateButtonClick(item: ChapterItem)
    }
}