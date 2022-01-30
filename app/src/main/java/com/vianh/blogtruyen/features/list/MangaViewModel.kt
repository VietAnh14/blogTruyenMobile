package com.vianh.blogtruyen.features.list

import androidx.collection.ArraySet
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.data.prefs.AppSettings
import com.vianh.blogtruyen.data.prefs.ListMode
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.list.data.CategoryRepo
import com.vianh.blogtruyen.features.list.filter.FilterCategoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

abstract class MangaViewModel(val settings: AppSettings, categoryRepo: CategoryRepo): BaseVM() {
    protected val filterCategories: MutableStateFlow<Set<String>> = MutableStateFlow(ArraySet())
    val categoryItems = combine(categoryRepo.observeAll(), filterCategories) { categories, filterCategories ->
        categories
            .sortedBy { it.name }
            .map {
                val selected = filterCategories.contains(it.name)
                FilterCategoryItem(it, selected)
            }
    }.asLiveData(Dispatchers.Default)

    protected val listMode = MutableStateFlow(settings.getListMode())
    protected val searchQuery = MutableStateFlow("")

    abstract val content: LiveData<List<ListItem>>

    abstract fun loadNextPage()
    abstract fun reload()

    fun saveListMode(listMode: ListMode) {
        settings.saveListMode(listMode)
        this.listMode.value = listMode
    }

    fun search(query: String?) {
        searchQuery.value = query.orEmpty()
    }

    open fun applyFilter() {
        val newFilters = categoryItems.value.orEmpty()
            .filter { it.isSelected }
            .mapTo(ArraySet()) { it.category.name }

        filterCategories.value = newFilters
        settings.saveFilterCategories(newFilters)
    }
}