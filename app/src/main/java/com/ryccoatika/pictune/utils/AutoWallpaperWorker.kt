package com.ryccoatika.pictune.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Rect
import android.net.Uri
import androidx.work.*
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.model.PhotoDetail
import com.ryccoatika.core.domain.usecase.HistoryUseCase
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import com.ryccoatika.core.utils.filename
import com.ryccoatika.core.utils.toPhotoMinimal
import com.ryccoatika.pictune.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit

class AutoWallpaperWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val unsplashInteractor: UnsplashUseCase by inject()
    private val historyInteractor: HistoryUseCase by inject()

    private val wallpaperSourcesValue =
        context.resources.getStringArray(R.array.wallpaper_sources_values)

    companion object {
        private const val AUTO_WALLPAPER_JOB_ID = "auto_wallpaper_job_id"
        private const val EXTRA_SCREEN_MODE = "extra_screen_mode"
        private const val EXTRA_WALLPAPER_SOURCE = "extra_wallpaper_source"
        private const val EXTRA_SEARCH_QUERY = "extra_search_query"

        fun startAutoWallpaper(context: Context, pictunePreferences: PictuneSharedPreferences) {
            val screen = pictunePreferences.screen
            val wallpaperSource = pictunePreferences.wallpaperSource
            val searchQuery = pictunePreferences.searchQuery
            val interval = pictunePreferences.interval
            val onWifi = pictunePreferences.onWifi
            val onCharging = pictunePreferences.onCharging
            val onIdle = pictunePreferences.onIdle

            // repeat interval for periodic work request
            val repeatInterval: Long
            val timeUnit = when (interval) {
                pictunePreferences.intervalValues[0] -> { // minutes
                    repeatInterval = pictunePreferences.minutes.toLong()
                    TimeUnit.MINUTES
                }
                pictunePreferences.intervalValues[1] -> { // hours
                    repeatInterval = pictunePreferences.hours.toLong()
                    TimeUnit.HOURS
                }
                pictunePreferences.intervalValues[2] -> { // days
                    repeatInterval = pictunePreferences.days.toLong()
                    TimeUnit.DAYS
                }
                else -> { // minutesx
                    repeatInterval = pictunePreferences.minutes.toLong()
                    TimeUnit.MINUTES
                }
            }

            // extra data pass to workmanager
            val data = Data.Builder().apply {
                putString(EXTRA_SCREEN_MODE, screen) // set screen to apply
                putString(EXTRA_WALLPAPER_SOURCE, wallpaperSource)
                putString(EXTRA_SEARCH_QUERY, searchQuery)
            }

            val constrains = Constraints.Builder().apply {
                setRequiredNetworkType(if (onWifi) NetworkType.UNMETERED else NetworkType.CONNECTED)
                setRequiresCharging(onCharging)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                    setRequiresDeviceIdle(onIdle)

            }

            val workRequest =
                PeriodicWorkRequestBuilder<AutoWallpaperWorker>(repeatInterval, timeUnit)
                    .setInputData(data.build())
                    .setConstraints(constrains.build())
                    .build()

            WorkManager.getInstance(context).cancelAllWorkByTag(AUTO_WALLPAPER_JOB_ID)
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                AUTO_WALLPAPER_JOB_ID,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(AUTO_WALLPAPER_JOB_ID)
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val screenMode = inputData.getString(EXTRA_SCREEN_MODE) ?: "both"
        val wallpaperSource = inputData.getString(EXTRA_WALLPAPER_SOURCE)
            ?: context.getString(R.string.default_screen_value)
        val searchQuery = inputData.getString(EXTRA_SEARCH_QUERY) ?: ""
        try {
            val result = when (wallpaperSource) {
                // unsplash - random
                wallpaperSourcesValue[0] -> {
                    unsplashInteractor.getRandomPhoto()
                }
                // unsplash - search
                wallpaperSourcesValue[1] -> {
                    unsplashInteractor.getRandomPhoto(query = searchQuery)
                }
                else -> {
                    unsplashInteractor.getRandomPhoto()
                }
            }
            when (result) {
                is Resource.Error,
                is Resource.Empty ->
                    return@withContext Result.retry()
                is Resource.Success -> {
                    result.data?.let {
                        downloadPhoto(context, it, screenMode)
                    }
                    return@withContext Result.success()
                }
                else -> return@withContext Result.retry()
            }
        } catch (e: Throwable) {
            return@withContext Result.failure()
        }
    }

    private suspend fun downloadPhoto(context: Context, photo: PhotoDetail, screenMode: String) =
        withContext(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                val downloadManager = DownloadManager(context)

                if (downloadManager.isFileExist(photo.filename)) {
                    val uri = downloadManager.getUriForPhoto(photo.filename)
                    uri?.let {
                        setWallpaper(it, screenMode, photo.getVisibleCropHint())
                        historyInteractor.addPhotoToHistory(photo.toPhotoMinimal())
                    }
                } else {
                    downloadManager.setOnCompletedListener {
                        GlobalScope.launch(Dispatchers.IO) {
                            setWallpaper(it, screenMode, photo.getVisibleCropHint())
                            historyInteractor.addPhotoToHistory(photo.toPhotoMinimal())
                            unsplashInteractor.triggerDownloadPhoto(photo.id)
                        }.start()
                    }
                    downloadManager.startDownload(photo.filename, photo.urls.full, false)
                }
            }
        }

    private fun setWallpaper(uri: Uri, screenMode: String, visibleCropHint: Rect?) {
        val wallpaperManager = WallpaperManager.getInstance(context)
        val screenValues: Array<String> = context.resources.getStringArray(R.array.screen_values)
        val inputStream = context.contentResolver.openInputStream(uri)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val screen = when (screenMode) {
                screenValues[0] -> WallpaperManager.FLAG_LOCK or WallpaperManager.FLAG_SYSTEM
                screenValues[1] -> WallpaperManager.FLAG_SYSTEM
                screenValues[2] -> WallpaperManager.FLAG_LOCK
                else -> WallpaperManager.FLAG_LOCK or WallpaperManager.FLAG_SYSTEM
            }
            wallpaperManager.setStream(inputStream, visibleCropHint, true, screen)
        } else
            wallpaperManager.setStream(inputStream)
        inputStream?.close()
    }
}