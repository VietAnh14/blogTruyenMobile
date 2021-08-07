package com.vianh.blogtruyen.features.details.info

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.ChapterItemBinding
import com.vianh.blogtruyen.features.download.DownloadState
import com.vianh.blogtruyen.features.details.info.adapter.ChapterItem
import com.vianh.blogtruyen.utils.getColorFromAttr
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

        binding.stateButton.setOnClickListener {
            onStateClick(clickListener)
        }
    }

    @SuppressLint("ResourceType")
    fun onBind(item: ChapterItem) {
        data = item
        binding.chapterName.text = item.chapter.name
        val textColor = if (item.chapter.read) {
            itemView.context.getColorFromAttr(android.R.attr.colorControlNormal)
        } else {
            itemView.context.getColorFromAttr(android.R.attr.textColorPrimary)
        }
        binding.chapterName.setTextColor(textColor)

        downloadJob = item.downloadState.onEach {
            when(it) {
                is DownloadState.NotDownloaded -> {
                    binding.stateButton.setImageResource(R.drawable.ic_file_download)
                }

                is DownloadState.Completed -> {
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
        fun onStateButtonClick(item: ChapterItem)
    }
}