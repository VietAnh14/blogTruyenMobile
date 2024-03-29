package com.vianh.blogtruyen.features.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.vianh.blogtruyen.BuildConfig
import com.vianh.blogtruyen.data.github.GithubRepo
import com.vianh.blogtruyen.data.github.Release
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.utils.*
import com.vianh.blogtruyen.utils.ext.asLiveDataDistinct
import com.vianh.blogtruyen.utils.ext.await
import com.vianh.blogtruyen.utils.ext.saveImageToGalley
import com.vianh.blogtruyen.utils.ext.toSafeFileName
import kotlinx.coroutines.Dispatchers

class MainViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val githubRepo: GithubRepo
) : BaseVM() {

    val saveImageCompleteMessage: SingleLiveEvent<String> = SingleLiveEvent()
    val newRelease: SingleLiveEvent<Release> = SingleLiveEvent()

    val notificationCount = favoriteRepository
        .getTotalNotificationCount()
        .asLiveDataDistinct(viewModelScope.coroutineContext + Dispatchers.Default)

    init {
        checkNewUpdate()
    }

    fun checkNewUpdate() {
        launchJob {
            val release = githubRepo.getLatestRelease()
            if (release.versionCode > BuildConfig.VERSION_CODE) {
                newRelease.call(release)
            }
        }
    }


    fun saveMangaCover(manga: Manga, appContext: Context) {
        launchJob(Dispatchers.IO) {
            val cover = Glide.with(appContext.applicationContext)
                .asBitmap()
                .await(manga.imageUrl) ?: throw IllegalStateException("Failed to save cover")

            val uri = saveImageToGalley(manga.title.toSafeFileName(), cover, appContext.contentResolver)
            if (uri != null) {
                saveImageCompleteMessage.postValue("Cover saved")
            } else {
                saveImageCompleteMessage.postValue("Failed to save cover")
            }
        }
    }
}