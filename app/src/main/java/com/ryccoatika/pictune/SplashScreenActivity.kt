package com.ryccoatika.pictune

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.preference.PreferenceManager
import com.ryccoatika.pictune.settings.ThemeHelper

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nightMode = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(getString(R.string.key_night_mode_switch), false)

        if (nightMode)
            ThemeHelper.applyTheme(ThemeHelper.DARK_MODE)
        else
            ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)

        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 1000)
    }
}
