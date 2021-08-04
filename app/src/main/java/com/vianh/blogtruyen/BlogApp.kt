package com.vianh.blogtruyen

import android.app.Application
import com.vianh.blogtruyen.di.appModule
import com.vianh.blogtruyen.di.viewModelModule
import com.vianh.blogtruyen.features.download.DownloadNotificationHelper
import com.vianh.blogtruyen.features.download.downloadModule
import com.vianh.blogtruyen.features.favorites.UpdateFavoriteWorker
import com.vianh.blogtruyen.features.favorites.favoriteModule
import com.vianh.blogtruyen.features.history.historyModule
import com.vianh.blogtruyen.features.local.localModule
import com.vianh.blogtruyen.features.mangaDetails.mangaInfo.infoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class BlogApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@BlogApp)
            modules(
                appModule,
                viewModelModule,
                historyModule,
                infoModule,
                favoriteModule,
                downloadModule,
                localModule
            )
        }

        DownloadNotificationHelper.createNotificationChannel(this)

        UpdateFavoriteWorker.setupUpdateWork(applicationContext)
    }
}