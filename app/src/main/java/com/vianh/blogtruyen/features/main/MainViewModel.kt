package com.vianh.blogtruyen.features.main

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.features.base.BaseVM

class MainViewModel: BaseVM() {

    val selectedTabPos = MutableLiveData<Int>(0)

    fun changeTab(pos: Int) {
        selectedTabPos.value = pos
    }
}