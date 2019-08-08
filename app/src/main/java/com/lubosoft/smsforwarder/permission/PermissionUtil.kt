package com.lubosoft.smsforwarder.permission

import android.content.Context
import android.content.Context.MODE_PRIVATE

object PermissionUtil {

    const val PREFS_FILE_NAME = "com.lubosoft.io.com.lubosoft.io.com.lubosoft.io.smsforwarder.permission.preferences"

    fun firstTimeAskingPermission(context: Context, permission: String, isFirstTime: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(permission, isFirstTime).apply()
    }

    fun isFirstTimeAskingPermission(context: Context, permission: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
        return sharedPreferences.getBoolean(permission, true)
    }
}