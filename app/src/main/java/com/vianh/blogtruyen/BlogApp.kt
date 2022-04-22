package com.vianh.blogtruyen

import android.app.Application
import com.vianh.blogtruyen.data.prefs.prefsModule
import com.vianh.blogtruyen.data.dataModule
import com.vianh.blogtruyen.di.viewModelModule
import com.vianh.blogtruyen.features.download.DownloadNotificationHelper
import com.vianh.blogtruyen.features.download.downloadModule
import com.vianh.blogtruyen.features.favorites.UpdateFavoriteWorker
import com.vianh.blogtruyen.features.favorites.favoriteModule
import com.vianh.blogtruyen.features.history.historyModule
import com.vianh.blogtruyen.features.local.localModule
import com.vianh.blogtruyen.features.details.infoModule
import com.vianh.blogtruyen.features.list.homeModule
import com.vianh.blogtruyen.features.search.searchModule
import com.vianh.blogtruyen.features.update.UpdateHelper
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
                prefsModule,
                dataModule,
                viewModelModule,
                historyModule,
                infoModule,
                favoriteModule,
                downloadModule,
                localModule,
                searchModule,
                homeModule
            )
        }

        DownloadNotificationHelper.createNotificationChannel(this)
        UpdateHelper.createNotificationChannel(this)

        UpdateFavoriteWorker.setupUpdateWork(applicationContext)
    }
}