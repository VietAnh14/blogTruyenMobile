package com.vianh.blogtruyen

import android.app.Application
import com.vianh.blogtruyen.di.appModule
import com.vianh.blogtruyen.di.viewModelModule
import com.vianh.blogtruyen.features.favorites.favoriteModule
import com.vianh.blogtruyen.features.history.historyModule
import com.vianh.blogtruyen.features.mangaDetails.mangaInfo.infoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MvvmApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@MvvmApp)
            modules(
                appModule,
                viewModelModule,
                historyModule,
                infoModule,
                favoriteModule
            )
        }
    }
}