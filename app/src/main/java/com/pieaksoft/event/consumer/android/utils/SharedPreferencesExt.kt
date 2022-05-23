package com.pieaksoft.event.consumer.android.utils

import android.content.SharedPreferences


/**
 * Preference keys
 */
enum class PrefKeys {
    RESET_CYCLE_START_DATE_TIME {
        override val key: String = "reset_cycle_date"
    };

    abstract val key: String
}

// Store reset cycle date
fun SharedPreferences.storeResetCycleStartDateTime(date: String?, time: String?) {
    put(PrefKeys.RESET_CYCLE_START_DATE_TIME, "$date $time")
}

// Get reset cycle date
fun SharedPreferences.getResetCycleStartDateTime(): String? {
    return getString(PrefKeys.RESET_CYCLE_START_DATE_TIME.key, null)
}


inline fun <reified T> SharedPreferences.get(key: PrefKeys, defaultValue: T?) {
    get(key.key, defaultValue)
}


inline fun <reified T> SharedPreferences.put(key: PrefKeys, value: T) {
    put(key.key, value)
}


inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T): T {
    when (T::class) {
        Boolean::class -> return this.getBoolean(key, defaultValue as Boolean) as T
        Float::class -> return this.getFloat(key, defaultValue as Float) as T
        Int::class -> return this.getInt(key, defaultValue as Int) as T
        Long::class -> return this.getLong(key, defaultValue as Long) as T
        String::class -> return this.getString(key, defaultValue as String) as T
        else -> {
            if (defaultValue is Set<*>) {
                return this.getStringSet(key, defaultValue as Set<String>) as T
            }
        }
    }

    return defaultValue
}

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    val editor = this.edit()

    when (T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String)
        else -> {
            if (value is Set<*>) {
                editor.putStringSet(key, value as Set<String>)
            }
        }
    }
    editor.apply()
}


