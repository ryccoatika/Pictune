package com.ryccoatika.pictune.settings.autowallpaper

import android.content.SharedPreferences
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceManager
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.activity_setting_auto_wallpaper.*
import java.util.concurrent.TimeUnit

class SettingAutoWallpaperActivity
    : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {

    private lateinit var keyMode: String
    private lateinit var keyScreen: String
    private lateinit var keyInterval: String
    private lateinit var keyMinutes: String
    private lateinit var keyHours: String
    private lateinit var keyDays: String
    private lateinit var keyOnWifi: String
    private lateinit var keyOnCharging: String
    private lateinit var keyOnIdle: String

    private lateinit var modeValues: Array<String>
    private lateinit var screenValues: Array<String>
    private lateinit var intervalValues: Array<String>

    private lateinit var sharedPreference: SharedPreferences
    private lateinit var workManagerTag: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_auto_wallpaper)

        workManagerTag = getString(R.string.auto_wallpaper_tag)
        setSupportActionBar(setting_auto_wallpaper_toolbar)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.setting_auto_wallpaper_fragment_container,
                SettingAutoWallpaperScreen()
            )
            .commit()

        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        val nightMode = sharedPreference.getBoolean(getString(R.string.key_night_mode_switch), false)
        if (nightMode)
            setting_auto_wallpaper_btn_apply.setTextColor(getColor(R.color.colorBlack))
        else
            setting_auto_wallpaper_btn_apply.setTextColor(getColor(R.color.colorWhite))

        keyMode = getString(R.string.key_mode)
        keyScreen = getString(R.string.key_apply_on_screen)
        keyInterval = getString(R.string.key_interval)
        keyMinutes = getString(R.string.key_interval_minutes)
        keyHours = getString(R.string.key_interval_hours)
        keyDays = getString(R.string.key_interval_days)
        keyOnIdle = getString(R.string.key_on_idle)
        keyOnWifi = getString(R.string.key_on_wifi)
        keyOnCharging = getString(R.string.key_on_charging)

        modeValues = resources.getStringArray(R.array.mode_values)
        screenValues = resources.getStringArray(R.array.screen_values)
        intervalValues = resources.getStringArray(R.array.interval_values)

        if (sharedPreference.getBoolean(getString(R.string.key_auto_wallpaper_switch), false))
            setting_auto_wallpaper_btn_apply.visibility = View.VISIBLE
        else
            setting_auto_wallpaper_btn_apply.visibility = View.INVISIBLE

        setting_auto_wallpaper_btn_apply.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        sharedPreference.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreference.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.key_auto_wallpaper_switch) -> {
                if (sharedPreference.getBoolean(getString(R.string.key_auto_wallpaper_switch), false))
                    setting_auto_wallpaper_btn_apply.visibility = View.VISIBLE
                else {
                    setting_auto_wallpaper_btn_apply.visibility = View.INVISIBLE
                    WorkManager.getInstance().cancelAllWorkByTag(workManagerTag)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        // get screen to apply preference
        val screenMode = sharedPreference.getString(keyScreen, screenValues[0]) ?: screenValues[0]
        // get interval mode from preference
        val interval = sharedPreference.getString(keyInterval, intervalValues[0]) ?: intervalValues[0]
        val onWifi = sharedPreference.getBoolean(keyOnWifi, false)
        val onCharging = sharedPreference.getBoolean(keyOnCharging, false)
        val onIdle = sharedPreference.getBoolean(keyOnIdle, false)

        // repeat interval for periodic work request
        val repeatInterval: Long
        val timeUnit = when (interval) {
            intervalValues[0] -> { // minutes
                repeatInterval = sharedPreference.getString(keyMinutes, "15")?.toLong() ?: 15
                TimeUnit.MINUTES
            }
            intervalValues[1] -> { // hours
                repeatInterval = sharedPreference.getString(keyHours, "1")?.toLong() ?: 1
                TimeUnit.HOURS
            }
            intervalValues[2] -> { // days
                repeatInterval = sharedPreference.getString(keyDays, "1")?.toLong() ?: 1
                TimeUnit.DAYS
            } else -> { // minutesx
                repeatInterval = sharedPreference.getString(keyMinutes, "15")?.toLong() ?: 15
                TimeUnit.MINUTES
            }
        }

        // extra data pass to workmanager
        val data = Data.Builder().apply {
            putString(AutoWallpaper.EXTRA_SCREEN, screenMode) // set screen to apply
        }

        val constrains = Constraints.Builder().apply {
            setRequiredNetworkType(if (onWifi) NetworkType.UNMETERED else NetworkType.CONNECTED)
            setRequiresCharging(onCharging)
            setRequiresDeviceIdle(onIdle)
        }

        val workRequest = PeriodicWorkRequest.Builder(
            AutoWallpaper::class.java,
            repeatInterval,
            timeUnit
        )

        when(v?.id) {
            R.id.setting_auto_wallpaper_btn_apply -> {
                when (sharedPreference.getString(keyMode, modeValues[0]) ?: modeValues[0]) {
                    modeValues[0] -> { // unsplash
                        data.putString(AutoWallpaper.EXTRA_MODE, AutoWallpaper.MODE_UNSPLASH_RANDOM)
                    }
                    modeValues[1] -> { // NASA
                        data.putString(AutoWallpaper.EXTRA_MODE, AutoWallpaper.MODE_POTD_NASA)
                    }
                    modeValues[2] -> { // bing
                        data.putString(AutoWallpaper.EXTRA_MODE, AutoWallpaper.MODE_POTD_BING)
                    }
                    modeValues[3] -> { // NatGeo
                        data.putString(AutoWallpaper.EXTRA_MODE, AutoWallpaper.MODE_POTD_NATGEO)
                    }
                }

                WorkManager.getInstance().cancelAllWorkByTag(workManagerTag)
                WorkManager.getInstance().enqueueUniquePeriodicWork(
                    workManagerTag,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest.apply {
                        addTag(workManagerTag)
                        setInputData(data.build())
                        setConstraints(constrains.build())
                    }.build()
                )
                Snackbar.make(
                    setting_auto_wallpaper_btn_apply,
                    getString(R.string.text_auto_wallpaper_applied),
                    Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
