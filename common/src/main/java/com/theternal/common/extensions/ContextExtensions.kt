package com.theternal.common.extensions

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.theternal.common.constants.PrefKeys.STORE

fun Context.getSavedBoolean(key: String, defValue: Boolean = false): Boolean {
    return getSharedPreferences(
        STORE, MODE_PRIVATE
    ).getBoolean(key, defValue)
}

fun Context.saveBoolean(
    key: String,
    value: Boolean,
    callback: ((Boolean) -> Unit)? = null
) {
    getSharedPreferences(STORE, MODE_PRIVATE).edit().apply {
        putBoolean(key, value)
        if(callback != null) {
            callback(commit())
        } else {
            apply()
        }
    }
}

fun Context.getSavedString(key: String, defValue: String? = null): String? {
    return getSharedPreferences(
        STORE, MODE_PRIVATE
    ).getString(key, defValue)
}

fun Context.saveString(
    key: String,
    value: String,
    callback: ((Boolean) -> Unit)? = null
) {
    getSharedPreferences(STORE, MODE_PRIVATE).edit().apply {
        putString(key, value)
        if(callback != null) {
            callback(commit())
        } else {
            apply()
        }
    }
}
