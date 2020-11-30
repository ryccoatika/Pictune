package com.ryccoatika.pictune

import android.app.Application
import com.bumptech.glide.Glide
import com.ryccoatika.core.di.localModule
import com.ryccoatika.core.di.networkModule
import com.ryccoatika.core.di.repositoryModule
import com.ryccoatika.core.di.useCaseModule
import com.ryccoatika.pictune.di.sharedPreferenceModule
import com.ryccoatika.pictune.di.viewModelModule
import com.ryccoatika.pictune.utils.PictuneSharedPreferences
import com.ryccoatika.pictune.utils.ThemeHelper
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PictuneApplication : Application() {

    private val pictunePreferences: PictuneSharedPreferences by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(
                viewModelModule,
                networkModule,
                localModule,
                repositoryModule,
                useCaseModule,
                sharedPreferenceModule
            )
        }

        val nightMode = pictunePreferences.theme
        ThemeHelper.applyTheme(nightMode)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(applicationContext).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(applicationContext).trimMemory(level)
    }
}