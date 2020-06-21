package com.vianh.blogtruyen

import android.app.Application
import com.androidnetworking.AndroidNetworking

class MvvmApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(this)
        app = this
    }

    companion object AppContext{
        lateinit var app: MvvmApp
    }
}