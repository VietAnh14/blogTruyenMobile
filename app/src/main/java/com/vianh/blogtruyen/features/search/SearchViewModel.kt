package com.vianh.blogtruyen.features.search

import com.vianh.blogtruyen.features.base.BaseVM
import kotlinx.coroutines.flow.MutableStateFlow

class SearchViewModel(initQuery: String?): BaseVM() {
    val searchQuery = MutableStateFlow(initQuery)
}