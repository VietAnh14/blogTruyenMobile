package com.vianh.blogtruyen.data.prefs

import android.content.Context
import androidx.core.content.edit
import timber.log.Timber

class AppSettings(context: Context) {
    companion object {
        const val PREFS_NAME = "app_settings"
        const val LIST_MODE_KEY = "list_mode"
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
}