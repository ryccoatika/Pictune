package com.ryccoatika.pictune.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.settings.autowallpaper.SettingAutoWallpaperActivity
import java.lang.Exception

class SettingsPreferenceScreen : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey)

        initSettings()
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun initSettings() {
        findPreference<Preference>(getString(R.string.key_app_version))?.summary = BuildConfig.VERSION_NAME

        val cache = StorageHandler.getCacheSize(requireContext())
        findPreference<Preference>(getString(R.string.key_clear_cache))
            ?.summary = getString(R.string.text_cache_total, cache)

        val data = StorageHandler.getDataSize(requireContext())
        findPreference<Preference>(getString(R.string.key_clear_data))
            ?.summary = getString(R.string.text_data_total, data)
    }



    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when(preference?.key) {
            getString(R.string.key_auto_wallpaper) -> {
                startActivity(Intent(requireContext(), SettingAutoWallpaperActivity::class.java))
                true
            }
            getString(R.string.key_github_page) -> {
                val openWeb = Intent(Intent.ACTION_VIEW)
                openWeb.data = Uri.parse(getString(R.string.link_github_page))
                startActivity(Intent.createChooser(openWeb, getString(R.string.text_open_with)))
                true
            }
            getString(R.string.key_unsplash_page) -> {
                val openWeb = Intent(Intent.ACTION_VIEW)
                openWeb.data = Uri.parse(getString(R.string.link_unsplash_page))
                startActivity(Intent.createChooser(openWeb, getString(R.string.text_open_with)))
                true
            }
            getString(R.string.key_rate_app) -> {
                try {
                    val rate = Intent(Intent.ACTION_VIEW)
                    rate.data = Uri.parse("market://details?id=${context?.packageName}")
                    startActivity(rate)
                } catch (e: Exception) {
                    Log.w("190401", "when rate clicked", e)
                }
                true
            }
            getString(R.string.key_report_bugs) -> {
                val openWeb = Intent(Intent.ACTION_VIEW)
                openWeb.data = Uri.parse(getString(R.string.link_report_bugs))
                startActivity(Intent.createChooser(openWeb, getString(R.string.text_open_with)))
                true
            }
            getString(R.string.key_author) -> {
                val openWeb = Intent(Intent.ACTION_VIEW)
                openWeb.data = Uri.parse(getString(R.string.link_portofolio))
                startActivity(Intent.createChooser(openWeb, getString(R.string.text_open_with)) )
                true
            }
            getString(R.string.key_clear_cache) -> {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setMessage(getString(R.string.text_are_you_sure))
                    setPositiveButton(getString(R.string.text_yes)
                    ) { _, _ ->
                        StorageHandler.clearCache(requireContext())
                        initSettings()
                    }
                    setNegativeButton(getString(R.string.text_no), null)
                }.show()
                true
            }
            getString(R.string.key_clear_data) -> {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setMessage(getString(R.string.text_are_you_sure))
                    setPositiveButton(getString(R.string.text_yes)
                    ) { _, _ ->
                        StorageHandler.clearData(requireContext())
                        initSettings()
                    }.setNegativeButton(getString(R.string.text_no), null)

                }.show()
                true
            }
            else -> false
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.key_night_mode_switch) -> {
                if (sharedPreferences?.getBoolean(getString(R.string.key_night_mode_switch), false) == true) {
                    ThemeHelper.applyTheme(ThemeHelper.DARK_MODE)
                } else {
                    ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
                }
            }
        }
    }
}