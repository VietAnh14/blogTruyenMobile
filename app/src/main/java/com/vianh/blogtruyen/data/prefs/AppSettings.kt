package com.vianh.blogtruyen.data.prefs

import android.content.Context
import androidx.core.content.edit
import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.utils.mapToSet
import org.json.JSONObject
import timber.log.Timber

class AppSettings(context: Context) {
    companion object {
        const val PREFS_NAME = "app_settings"
        const val LIST_MODE_KEY = "list_mode"
        const val READER_MODE_KEY = "reader_mode"
        const val KEEP_SCREEN_ON = "screen_on"
        const val FILTER_CATEGORIES = "filter_categories"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveListMode(listMode: ListMode) {
        prefs.edit {
            putString(LIST_MODE_KEY, listMode.name)
        }
    }

    fun getListMode(): ListMode {
        return try {
            val listMode = prefs.getString(LIST_MODE_KEY, ListMode.GRID.name)
            ListMode.valueOf(listMode!!)
        } catch (e: Exception) {
            Timber.e(e)
            ListMode.GRID
        }
    }

    fun getReaderMode(): ReaderMode {
        val modeString = prefs.getString(READER_MODE_KEY, null) ?: ReaderMode.VERTICAL.name
        return ReaderMode.valueOf(modeString)
    }

    fun setReaderMode(value: ReaderMode) {
        prefs.edit { putString(READER_MODE_KEY, value.name) }
    }

    fun getKeepScreenOn(): Boolean {
        return prefs.getBoolean(KEEP_SCREEN_ON, false)
    }

    fun setKeepScreenOn(value: Boolean) {
        prefs.edit { putBoolean(KEEP_SCREEN_ON, value) }
    }

    fun getFilterCategories(): Set<String> {
        return prefs.getStringSet(FILTER_CATEGORIES, emptySet()) ?: emptySet()
    }

    fun saveFilterCategories(categories: Set<String>?) {
        if (categories.isNullOrEmpty()) {
            prefs.edit { remove(FILTER_CATEGORIES) }
            return
        }

        prefs.edit { putStringSet(FILTER_CATEGORIES, categories) }
    }
}