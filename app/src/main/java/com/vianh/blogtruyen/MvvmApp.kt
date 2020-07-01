package com.vianh.blogtruyen

import android.app.Application

class MvvmApp: Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object AppContext{
        lateinit var app: MvvmApp
    }
}