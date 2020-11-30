package com.ryccoatika.pictune.settings.autowallpaper

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.text.isDigitsOnly
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.settings.autowallpaper.history.HistoryActivity
import com.ryccoatika.pictune.utils.showToast

class AutoWallpaperScreen : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var keyMode: String
    private lateinit var keySearchQuery: String
    private lateinit var keyInterval: String
    private lateinit var keyIntervalMinutes: String
    private lateinit var keyIntervalHours: String
    private lateinit var keyIntervalDays: String
    private lateinit var keyHistory: String

    private lateinit var modeValues: Array<String>
    private lateinit var intervalValues: Array<String>

    private var preferenceInterval: ListPreference? = null
    private var preferenceIntervalMinutes: EditTextPreference? = null
    private var preferenceIntervalHours: EditTextPreference? = null
    private var preferenceIntervalDays: EditTextPreference? = null
    private var preferenceSearchQuery: EditTextPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.auto_wallpaper_preferences, rootKey)

        keyMode = getString(R.string.key_wallpaper_source)
        keySearchQuery = getString(R.string.key_search_query)
        keyInterval = getString(R.string.key_interval)
        keyIntervalMinutes = getString(R.string.key_interval_minutes)
        keyIntervalHours = getString(R.string.key_interval_hours)
        keyIntervalDays = getString(R.string.key_interval_days)
        keyHistory = getString(R.string.key_history)
        modeValues = resources.getStringArray(R.array.wallpaper_sources_values)
        intervalValues = resources.getStringArray(R.array.interval_values)

        preferenceInterval = findPreference(keyInterval)
        preferenceIntervalMinutes = findPreference(keyIntervalMinutes)
        preferenceIntervalHours = findPreference(keyIntervalHours)
        preferenceIntervalDays = findPreference(keyIntervalDays)
        preferenceSearchQuery = findPreference(keySearchQuery)

        preferenceSearchQuery?.summary = preferenceSearchQuery?.text
        preferenceIntervalHours?.summary = preferenceIntervalHours?.text
        preferenceIntervalDays?.summary = preferenceIntervalDays?.text
        preferenceIntervalMinutes?.summary = preferenceIntervalMinutes?.text

        updateIntervalVisibility(preferenceScreen.sharedPreferences)
        updateSourceVisibility(preferenceScreen.sharedPreferences)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            keyHistory -> {
                startActivity(Intent(requireContext(), HistoryActivity::class.java))
                true
            }
            else -> false
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            keyMode -> updateSourceVisibility(sharedPreferences)
            keySearchQuery -> {
                val query = sharedPreferences?.getString(keySearchQuery, "Wallpaper") ?: "Wallpaper"
                if (query.isEmpty()) {
                    context?.showToast(getString(R.string.cannot_be_empty))
                    preferenceSearchQuery?.text = "Wallpaper"
                }
                preferenceSearchQuery?.summary = preferenceSearchQuery?.text
            }
            keyIntervalMinutes -> {
                val minutes = sharedPreferences?.getString(keyIntervalMinutes, "15") ?: "15"
                if (!minutes.isDigitsOnly()) {
                    context?.showToast(getString(R.string.digits_only))
                    preferenceIntervalMinutes?.text = "15"
                    return
                }
                if (minutes.toInt() < 15) {
                    context?.showToast(getString(R.string.must_greater_than_15))
                    preferenceIntervalMinutes?.text = "15"
                }
                preferenceIntervalMinutes?.summary = preferenceIntervalMinutes?.text
            }
            keyIntervalHours -> {
                val hours = sharedPreferences?.getString(keyIntervalHours, "1") ?: "1"
                if (!hours.isDigitsOnly()) {
                    context?.showToast(getString(R.string.digits_only))
                    preferenceIntervalHours?.text = "1"
                    return
                }
                if (hours.toInt() < 1) {
                    context?.showToast(getString(R.string.cant_below_1))
                    preferenceIntervalHours?.text = "1"
                }
                preferenceIntervalHours?.summary = preferenceIntervalHours?.text
            }
            keyIntervalDays -> {
                val days = sharedPreferences?.getString(keyIntervalDays, "1") ?: "1"
                if (!days.isDigitsOnly()) {
                    context?.showToast(getString(R.string.digits_only))
                    preferenceIntervalDays?.text = "1"
                    return
                }
                if (days.toInt() < 1) {
                    context?.showToast(getString(R.string.cant_below_1))
                    preferenceIntervalDays?.text = "1"
                }
                preferenceIntervalDays?.summary = preferenceIntervalDays?.text
            }
            keyInterval -> updateIntervalVisibility(sharedPreferences)
        }
    }

    private fun updateSourceVisibility(sharedPreferences: SharedPreferences?) {
        when (sharedPreferences?.getString(keyMode, modeValues[0]) ?: modeValues[0]) {
            // unsplash - random
            modeValues[0] -> preferenceSearchQuery?.isVisible = false
            // unsplash - search
            modeValues[1] -> preferenceSearchQuery?.isVisible = true
        }
    }

    private fun updateIntervalVisibility(sharedPreferences: SharedPreferences?) {
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
    }
}
