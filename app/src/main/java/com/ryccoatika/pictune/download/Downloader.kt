package com.ryccoatika.pictune.download

import android.os.AsyncTask
import android.util.Log
import com.ryccoatika.pictune.db.NetworkAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.lang.Exception
import java.net.URL

abstract class Downloader {

    abstract fun onStart()
    abstract fun onFailed(error: Throwable)
    abstract fun onDownloadStart(call: Call<ResponseBody>, file: File): Boolean
    abstract fun onProgress(progress: Int)
    abstract fun onCancelled()
    abstract fun onDownloaded(absolutePath: String)

    fun startDownload(directory: String, filename: String, url: String) {
        File(directory).also {
            if (!it.exists()) it.mkdirs()
        }

        onStart()

        val dataSource = NetworkAdapter.providesHttpAdapter(URL("http://localhost/"), true)
            .create(DownloadDataSource::class.java)

        dataSource.downloadImage(url).enqueue(object: retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.w("190401", "DownloadImage.kt:downloadImageToFile:onFailure", t)
                onFailed(t) // callback
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val contentType = response.body()?.contentType()
                    if (contentType?.type() == "image") {
                        try {
                            val file = File(directory, filename)

                            if (!onDownloadStart(call, file)) { // callback
                                return
                            }

                            Async {
                                SaveToDisk.writeResponseBodyToExternal(
                                    response.body(),
                                    file
                                ) { progress ->
                                    onProgress(progress) // callback
                                }

                                // when download success
                                if (!call.isCanceled) {
                                    onDownloaded(file.absolutePath) // callback
                                } else {
                                    file.delete()
                                    onCancelled() // callback
                                }
                            }.execute()
                        } catch (e: Exception) {
                            onFailed(e) // callback
                        }
                    }
                } else {
                    onFailed(Throwable(response.errorBody().toString())) // callback
                }
            }
        })
    }

    class Async(val handler: () -> Unit) : AsyncTask<Void, Void, String?>() {
        override fun doInBackground(vararg params: Void?): String? {
            handler()
            return null
        }
    }
}