package com.ryccoatika.pictune.settings.autowallpaper

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.potd.PotdDataSource
import com.ryccoatika.pictune.db.UnsplashDataSource
import com.ryccoatika.pictune.download.Downloader
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File
import java.net.URL
class AutoWallpaper(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    companion object {
        const val EXTRA_MODE = "mode"
        const val EXTRA_SCREEN = "screen"

        const val MODE_UNSPLASH_RANDOM = "unsplash_random"
        const val MODE_POTD_NASA = "potd_nasa"
        const val MODE_POTD_NATGEO = "potd_natgeo"
        const val MODE_POTD_BING = "potd_bing"
    }

    private val unsplashDataSource by lazy {
        NetworkAdapter.providesHttpAdapter(
            URL(BuildConfig.UNSPLASH_ENDPOIN)
            , false).create(UnsplashDataSource::class.java)
    }
    private val potdDataSource by lazy {
        NetworkAdapter.providesHttpAdapter(
            URL("https://localhost/"),
            false
        ).create(PotdDataSource::class.java)
    }

    private var call : Call<ResponseBody>? = null

    private val directory by lazy {
        (context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: "") + File.separator + "Wallpapers"
    }

    private val screenValues by lazy {
        context.resources.getStringArray(R.array.screen_values)
    }

    private val screenMode by lazy {
        inputData.getString(EXTRA_SCREEN) ?: screenValues[0]
    }

    private val downloader by lazy {
        object : Downloader() {
            override fun onStart() {
            }
            override fun onFailed(error: Throwable) {
                Log.w("190401", "AutoWallpaper:unsplashrandom:Downloader:onFailed", error)
            }

            override fun onDownloadStart(call: Call<ResponseBody>, file: File): Boolean {
                this@AutoWallpaper.call = call
                return if (!file.exists()) {
                    file.createNewFile()
                    true
                }else {
                    setWallpaper(file.absolutePath, screenMode)
                    false
                }
            }

            override fun onProgress(progress: Int) {
            }
            override fun onCancelled() {
            }

            override fun onDownloaded(absolutePath: String) {
                setWallpaper(absolutePath, screenMode)
            }
        }
    }

    private val disposable = CompositeDisposable()

    override fun doWork(): Result {

        if (isStopped)
            call?.cancel()

        when (inputData.getString(EXTRA_MODE)) {
            MODE_UNSPLASH_RANDOM -> {
                unsplashDataSource.getRandomPhoto().observeOn(Schedulers.io())
                    .toFlowable()
                    .subscribe({ photo ->
                        var filename = photo.user?.name?.toLowerCase()?.replace(" ", "-")
                        filename = applicationContext.getString(R.string.download_filename, filename, photo.id)

                        downloader.startDownload(directory, filename, photo.urls?.full ?: "")
                    }, { error ->
                        Log.w("190401", "AutoWallpaper:getRandomPhoto:subscribe:error", error)
                    }).let(disposable::add)
            }
            MODE_POTD_NASA -> {
                potdDataSource.getNasaPotd().observeOn(Schedulers.io())
                    .toFlowable()
                    .subscribe({ potdNasa ->
                        val filename = potdNasa.hdurl?.split('/')?.last() ?: "${potdNasa.hdurl}.jpg"
                        downloader.startDownload(directory, filename, potdNasa.hdurl ?: "")
                    }, { error ->
                        Log.w("190401", "AutoWallpaper:getNasaPotd:subscribe:error", error)
                    }).let(disposable::add)
            }
            MODE_POTD_NATGEO -> {
                potdDataSource.getNatGeoPotd().observeOn(Schedulers.io())
                    .toFlowable()
                    .subscribe({ potdNatGeo ->
                        val url = potdNatGeo.items?.first()?.image?.uri
                        val filename = url?.split('/')?.last()
                        url?.let {
                            downloader.startDownload(directory, filename ?: "", it)
                        }
                    }, { error ->
                        Log.w("190401", "AutoWallpaper:getNatGeoPotd:subscribe:error", error)
                    }).let(disposable::add)
            }
            MODE_POTD_BING -> {
                potdDataSource.getBingPotd().observeOn(Schedulers.io())
                    .toFlowable()
                    .subscribe({ potdBing ->
                        val url = "https://bing.com${potdBing.images?.first()?.url}"
                        val filename = "potd-bing.jpg"
                        downloader.startDownload(directory, filename, url)
                    }, { error ->
                        Log.w("190401", "AutoWallpaper:getBingPotd:subscribe:error", error)
                    }).let(disposable::add)
            }
        }
        return Result.success()
    }

    override fun onStopped() {
        call?.cancel()
        super.onStopped()
        disposable.dispose()
        disposable.clear()
    }

    private fun setWallpaper(path: String, screen: String) {
        val wallpaper = Wallpaper(applicationContext)
        val bitmap = BitmapFactory.decodeFile(path)
        when (screen) {
            screenValues[0] -> {
                wallpaper.setHomeScreenWallpaper(bitmap)
                wallpaper.setLockScreenWallpaper(bitmap)
            }
            screenValues[1] -> {
                wallpaper.setHomeScreenWallpaper(bitmap)
            }
            screenValues[2] -> {
                wallpaper.setLockScreenWallpaper(bitmap)
            }
        }
    }
}

