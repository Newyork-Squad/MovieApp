package com.karrar.movieapp.data.local

import android.content.Context
import androidx.core.content.edit

class SharedPreferences(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }


    companion object {
        const val SHARED_PREFS_NAME = "settings"
    }
}