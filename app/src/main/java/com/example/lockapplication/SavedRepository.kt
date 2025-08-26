package com.example.lockapplication

import android.content.Context
import androidx.core.content.edit

object SavedRepository {
    private const val NAME = "app_prefs"
    private const val KEY_SERVICE_RUNNING = "key_service_running"

    private fun prefs(
        context: Context
    ) = context.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun setServiceRunning(context: Context, running: Boolean) {
        prefs(context).edit { putBoolean(KEY_SERVICE_RUNNING, running) } // apply=비동기 커밋
    }

    fun isServiceRunning(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SERVICE_RUNNING, false)

}