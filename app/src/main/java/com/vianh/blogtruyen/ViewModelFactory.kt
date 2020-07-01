package com.vianh.blogtruyen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vianh.blogtruyen.data.AppDataManager
import com.vianh.blogtruyen.ui.main.MainViewModel
import com.vianh.blogtruyen.ui.mangaInfo.MangaInfoViewModel
import com.vianh.blogtruyen.ui.mangaViewer.MangaViewerViewModel

class ViewModelFactory: ViewModelProvider.Factory {
    val dataManager = AppDataManager.INSTANCE

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(dataManager)
            modelClass.isAssignableFrom(MangaInfoViewModel::class.java) -> MangaInfoViewModel(dataManager)
            modelClass.isAssignableFrom(MangaViewerViewModel::class.java) -> MangaViewerViewModel(dataManager)
            else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        } as T
    }
}