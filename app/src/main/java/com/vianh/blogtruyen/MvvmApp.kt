package com.vianh.blogtruyen

import android.app.Application
import com.vianh.blogtruyen.di.appModule
import com.vianh.blogtruyen.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MvvmApp: Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        startKoin {
            androidContext(this@MvvmApp)
            modules(appModule, viewModelModule)
        }
    }

    companion object AppContext{
        lateinit var app: MvvmApp
    }
}