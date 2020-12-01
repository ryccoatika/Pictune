package com.ryccoatika.pictune.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.settings.autowallpaper.AutoWallpaperActivity
import com.ryccoatika.pictune.utils.ThemeHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            (requireActivity() as AppCompatActivity).supportActionBar?.hide()

            childFragmentManager
                .beginTransaction()
                .replace(R.id.settings_fragment_container, SettingPreferenceScreen())
                .commit()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    class SettingPreferenceScreen : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        private val viewModel: SettingViewModel by viewModel()

        private lateinit var keyAutoWallpaper: String
        private lateinit var keyTheme: String
        private lateinit var keyGithubPage: String
        private lateinit var keyUnsplashWeb: String
        private lateinit var keyRateApp: String
        private lateinit var keyReportBug: String
        private lateinit var keyAuthor: String
        private lateinit var keyClearCache: String
        private lateinit var keyLicenses: String
        private lateinit var keyAppVersion: String

        private lateinit var defaultThemValue: String

        private var appVersionPref: Preference? = null
        private var clearCachePref: Preference? = null
        private var themePref: ListPreference? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.setting_preferences, rootKey)

            keyAutoWallpaper = getString(R.string.key_auto_wallpaper)
            keyTheme = getString(R.string.key_theme)
            keyGithubPage = getString(R.string.key_github_page)
            keyUnsplashWeb = getString(R.string.key_unsplash_page)
            keyRateApp = getString(R.string.key_rate_app)
            keyReportBug = getString(R.string.key_report_bugs)
            keyAuthor = getString(R.string.key_author)
            keyClearCache = getString(R.string.key_clear_cache)
            keyLicenses = getString(R.string.key_licenses)
            keyAppVersion = getString(R.string.key_app_version)

            defaultThemValue = getString(R.string.default_theme_value)

            appVersionPref = findPreference(keyAppVersion)
            clearCachePref = findPreference(keyClearCache)
            themePref = findPreference(keyTheme)

            appVersionPref?.summary = BuildConfig.VERSION_NAME
            viewModel.cacheSize.observe(this) { cache ->
                clearCachePref?.summary = getString(R.string.cache_total, cache.toString())
            }
            themePref?.summary = themePref?.entry
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
                keyAutoWallpaper -> {
                    startActivity(Intent(requireContext(), AutoWallpaperActivity::class.java))
                    true
                }
                keyGithubPage -> {
                    val openWeb = Intent(Intent.ACTION_VIEW)
                    openWeb.data = Uri.parse(getString(R.string.link_github_page))
                    startActivity(Intent.createChooser(openWeb, getString(R.string.open_with)))
                    true
                }
                keyUnsplashWeb -> {
                    val openWeb = Intent(Intent.ACTION_VIEW)
                    openWeb.data = Uri.parse(getString(R.string.link_unsplash_page))
                    startActivity(Intent.createChooser(openWeb, getString(R.string.open_with)))
                    true
                }
                keyRateApp -> {
                    try {
                        val rate = Intent(Intent.ACTION_VIEW)
                        rate.data = Uri.parse("market://details?id=${context?.packageName}")
                        startActivity(rate)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    true
                }
                keyReportBug -> {
                    val openWeb = Intent(Intent.ACTION_VIEW)
                    openWeb.data = Uri.parse(getString(R.string.link_report_bugs))
                    startActivity(Intent.createChooser(openWeb, getString(R.string.open_with)))
                    true
                }
                keyAuthor -> {
                    val openWeb = Intent(Intent.ACTION_VIEW)
                    openWeb.data = Uri.parse(getString(R.string.link_portofolio))
                    startActivity(Intent.createChooser(openWeb, getString(R.string.open_with)))
                    true
                }
                keyClearCache -> {
                    viewModel.clearCache()
                    true
                }
                keyLicenses -> {
                    startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
                    true
                }
                else -> false
            }
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            when (key) {
                keyTheme -> {
                    themePref?.summary = themePref?.entry
                    val theme = sharedPreferences?.getString(keyTheme, defaultThemValue)
                    ThemeHelper.applyTheme(theme)
                }
            }
        }
    }
}
