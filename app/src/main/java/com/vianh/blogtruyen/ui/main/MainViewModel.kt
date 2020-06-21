package com.vianh.blogtruyen.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.PreviewManga
import com.vianh.blogtruyen.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(dataManager: DataManager) : BaseViewModel(dataManager) {
    val _items = MutableLiveData<List<PreviewManga>>()

    fun getListManga() {
        uiScope.launch {
            try {
                val data = dataManager.getMangaProvider().fetchNewManga()
                _items.value = data
                Log.d(TAG, _items.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
            }
        }
    }
}