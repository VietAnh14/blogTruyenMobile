package com.vianh.blogtruyen.di

import android.content.Context
import com.vianh.blogtruyen.MvvmApp
import com.vianh.blogtruyen.ui.home.HomeViewModel
import com.vianh.blogtruyen.utils.BlogTruyenInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File

val viewModelModule
    get() = module {
        viewModel { HomeViewModel(get()) }
    }