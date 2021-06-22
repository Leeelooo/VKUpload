package com.leeloo.vkupload.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

private const val SETTINGS_PREFS_NAME = "VIDEO_UPLOAD_SETTINGS_PREFS"
private const val SETTING_UPLOAD_MODE = "UPLOAD_MODE"

private lateinit var prefs: SharedPreferences

fun createPrefs(context: Context) {
    prefs = context.getSharedPreferences(SETTINGS_PREFS_NAME, MODE_PRIVATE)
    if (!prefs.contains(SETTING_UPLOAD_MODE)) {
        prefs.edit {
            putBoolean(SETTING_UPLOAD_MODE, false)
        }
    }
}

fun isInStopMode() = prefs.getBoolean(SETTING_UPLOAD_MODE, false)

fun changeToStopMode() = prefs.edit { putBoolean(SETTING_UPLOAD_MODE, false) }

fun changeToUnstoppableMode() = prefs.edit { putBoolean(SETTING_UPLOAD_MODE, true) }