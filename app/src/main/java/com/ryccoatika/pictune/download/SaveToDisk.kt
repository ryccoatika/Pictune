package com.ryccoatika.pictune.download

import android.util.Log
import okhttp3.ResponseBody
import java.io.*

class SaveToDisk {
    companion object {
        fun writeResponseBodyToExternal(
            responseBody: ResponseBody?,
            file: File,
            callback: (progress: Int) -> Unit
        ): Boolean {
            responseBody?.let { response ->
                try {
                    Log.d("190401", file.absolutePath)
                    var inputStream: InputStream? = null
                    var outputStream: OutputStream? = null

                    try {
                        val fileReader = ByteArray(4096)
                        val fileSize = response.contentLength()
                        var fileSizeDownloaded = 0L

                        inputStream = response.byteStream()
                        outputStream = FileOutputStream(file)

                        while (true) {
                            val read = inputStream.read(fileReader)

                            if (read == -1) break

                            outputStream.write(fileReader, 0, read)
                            fileSizeDownloaded += read.toLong()

                            // calculeate progress percentage
                            val downloadPercent: Int =
                                ((fileSizeDownloaded.toDouble() / fileSize.toDouble()) * 100).toInt()
                            callback(downloadPercent)
                        }
                        outputStream.flush()
                        return true
                    } catch (e: IOException) {
                        Log.w("190401", "DownloadImage.kt:writeResponseBodyToDisk", e.cause)
                        return false
                    } finally {
                        inputStream?.close()
                        outputStream?.close()
                    }
                } catch (e: IOException) {
                    Log.w("190401", "DownloadImage.kt:writeResponseBodyToDisk", e.cause)
                    return false
                }
            }
            return false
        }
    }
}