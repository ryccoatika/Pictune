package com.ryccoatika.pictune.settings.autowallpaper

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.ryccoatika.pictune.R

class SettingAutoWallpaperScreen : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var keyMode: String
    private lateinit var keyInterval: String
    private lateinit var keyIntervalMinutes: String
    private lateinit var keyIntervalHours: String
    private lateinit var keyIntervalDays: String

    private lateinit var modeValues: Array<String>
    private lateinit var intervalValues: Array<String>

    private var preferenceInterval: ListPreference? = null
    private var preferenceIntervalMinutes: EditTextPreference? = null
    private var preferenceIntervalHours: EditTextPreference? = null
    private var preferenceIntervalDays: EditTextPreference? = null


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.auto_wallpaper_preferences, rootKey)

        keyMode = getString(R.string.key_mode)
        keyInterval = getString(R.string.key_interval)
        keyIntervalMinutes = getString(R.string.key_interval_minutes)
        keyIntervalHours = getString(R.string.key_interval_hours)
        keyIntervalDays = getString(R.string.key_interval_days)

        modeValues = resources.getStringArray(R.array.mode_values)
        intervalValues = resources.getStringArray(R.array.interval_values)

        preferenceInterval = findPreference(keyInterval)
        preferenceIntervalMinutes = findPreference(keyIntervalMinutes)
        preferenceIntervalHours = findPreference(keyIntervalHours)
        preferenceIntervalDays = findPreference(keyIntervalDays)

        updateVisibility(preferenceScreen.sharedPreferences)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            keyIntervalMinutes -> {
                val minutes = sharedPreferences?.getString(keyIntervalMinutes, "15") ?: "15"
                if (!minutes.isDigitsOnly()) {
                    showToast("Digits Only")
                    preferenceIntervalMinutes?.text = "15"
                    return
                }
                if (minutes.toInt() < 15) {
                    showToast("Must be 15 minutes or above")
                    preferenceIntervalMinutes?.text = "15"
                }
            }
            keyIntervalHours -> {
                val hours = sharedPreferences?.getString(keyIntervalHours, "1") ?: "1"
                if (!hours.isDigitsOnly()) {
                    showToast("Digits Only")
                    preferenceIntervalHours?.text = "1"
                    return
                }
                if (hours.toInt() < 1) {
                    showToast("can't below 1")
                    preferenceIntervalHours?.text = "1"
                }
            }
            keyIntervalDays -> {
                val days = sharedPreferences?.getString(keyIntervalDays, "1") ?: "1"
                if (!days.isDigitsOnly()) {
                    showToast("Digits Only")
                    preferenceIntervalDays?.text = "1"
                    return
                }
                if (days.toInt() < 1) {
                    showToast("can't below 1")
                    preferenceIntervalDays?.text = "1"
                }
            }
        }
        updateVisibility(sharedPreferences)
    }

    private fun updateVisibility(sharedPreferences: SharedPreferences?) {
        // hide interval options if sources is not from unsplash
        val mode = sharedPreferences?.getString(keyMode, modeValues[0]) ?: modeValues[0]
        preferenceInterval?.isVisible = (mode == modeValues[0])

        // hide hours and days interval if interval invisible
        if (preferenceInterval?.isVisible == true) {
            when (sharedPreferences?.getString(keyInterval, intervalValues[0]) ?: intervalValues[0]) {
                intervalValues[0] -> {
                    preferenceIntervalMinutes?.isVisible = true
                    preferenceIntervalHours?.isVisible = false
                    preferenceIntervalDays?.isVisible = false
                }
                intervalValues[1] -> {
                    preferenceIntervalMinutes?.isVisible = false
                    preferenceIntervalHours?.isVisible = true
                    preferenceIntervalDays?.isVisible = false
                }
                intervalValues[2] -> {
                    preferenceIntervalMinutes?.isVisible = false
                    preferenceIntervalHours?.isVisible = false
                    preferenceIntervalDays?.isVisible = true
                }
            }
        } else {
            preferenceIntervalMinutes?.isVisible = false
            preferenceIntervalHours?.isVisible = false
            preferenceIntervalDays?.isVisible = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
