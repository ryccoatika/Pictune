package com.ryccoatika.pictune.settings.autowallpaper

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.utils.AutoWallpaperWorker
import com.ryccoatika.pictune.utils.PictuneSharedPreferences
import kotlinx.android.synthetic.main.activity_auto_wallpaper.*
import org.koin.android.ext.android.inject

class AutoWallpaperActivity
    : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val pictunePreferences: PictuneSharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_wallpaper)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container,
                AutoWallpaperScreen()
            )
            .commit()

        btn_apply.isVisible = pictunePreferences.autoWallpaperEnabled

        btn_apply.setOnClickListener {
            AutoWallpaperWorker.startAutoWallpaper(this, pictunePreferences)
            Snackbar.make(
                constraint_layout,
                getString(R.string.setting_auto_wallpaper),
                Snackbar.LENGTH_LONG
            )
                .setAnchorView(btn_apply)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        pictunePreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        pictunePreferences.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.key_auto_wallpaper_switch) -> {
                if (pictunePreferences.autoWallpaperEnabled)
                    btn_apply.isVisible = true
                else {
                    btn_apply.isVisible = false
                    AutoWallpaperWorker.cancelWork(this)
                }
            }
        }
    }
}
