package com.ryccoatika.pictune.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.ryccoatika.pictune.R

class PictuneSharedPreferences(
    context: Context
) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    // VALUES
    val intervalValues: Array<String> = context.resources.getStringArray(R.array.interval_values)

    // SETTING
    private val keyTheme: String = context.getString(R.string.key_theme)
    private val defaultTheme: String = context.getString(R.string.default_theme_value)

    val theme: String
        get() = sharedPreferences.getString(keyTheme, defaultTheme) ?: defaultTheme

    // AUTO WALLPAPER
    private val keyAutoWallpaperEnabled: String =
        context.getString(R.string.key_auto_wallpaper_switch)
    private val keyWallpaperSource: String = context.getString(R.string.key_wallpaper_source)
    private val defaultWallpaperSource: String =
        context.getString(R.string.default_wallpaper_source_value)
    private val keySearchQuery: String = context.getString(R.string.key_search_query)

    // condition
    private val keyOnWifi: String = context.getString(R.string.key_on_wifi)
    private val keyOnCharging: String = context.getString(R.string.key_on_charging)
    private val keyOnIdle: String = context.getString(R.string.key_on_idle)

    // interval
    private val keyInterval: String = context.getString(R.string.key_interval)
    private val defaultInterval: String = context.getString(R.string.default_interval_value)
    private val keyMinutes: String = context.getString(R.string.key_interval_minutes)
    private val keyHours: String = context.getString(R.string.key_interval_hours)
    private val keyDays: String = context.getString(R.string.key_interval_days)

    // options
    private val keyScreen: String = context.getString(R.string.key_apply_on_screen)
    private val defaultScreen: String = context.getString(R.string.default_screen_value)

    val autoWallpaperEnabled: Boolean
        get() = sharedPreferences.getBoolean(keyAutoWallpaperEnabled, false)

    val wallpaperSource: String
        get() = sharedPreferences.getString(keyWallpaperSource, defaultWallpaperSource)
            ?: defaultWallpaperSource

    val searchQuery: String
        get() = sharedPreferences.getString(keySearchQuery, "") ?: ""

    val onWifi: Boolean
        get() = sharedPreferences.getBoolean(keyOnWifi, false)

    val onCharging: Boolean
        get() = sharedPreferences.getBoolean(keyOnCharging, false)

    val onIdle: Boolean
        get() = sharedPreferences.getBoolean(keyOnIdle, false)

    val interval: String
        get() = sharedPreferences.getString(keyInterval, defaultInterval) ?: defaultInterval

    val minutes: String
        get() = sharedPreferences.getString(keyMinutes, "15") ?: "15"

    val hours: String
        get() = sharedPreferences.getString(keyHours, "1") ?: "1"

    val days: String
        get() = sharedPreferences.getString(keyDays, "1") ?: "1"

    val screen: String
        get() = sharedPreferences.getString(keyScreen, defaultScreen) ?: defaultScreen
}
