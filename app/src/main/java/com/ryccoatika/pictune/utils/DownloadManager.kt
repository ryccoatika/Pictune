package com.ryccoatika.pictune.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import com.ryccoatika.pictune.R
import kotlinx.coroutines.*
import java.io.File

class DownloadManager(
    private val context: Context
) {

    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    private var downloadId: Long = 0

    private var onCompletedListener: ((Uri) -> Unit)? = null
    private val folderName = context.getString(R.string.app_name)
    private val pictunePath = "${
        Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
    }${File.separator}$folderName"

    fun setOnCompletedListener(listener: (Uri) -> Unit) {
        this@DownloadManager.onCompletedListener = listener
    }

    fun isFileExist(filename: String): Boolean {
        val file = File(pictunePath, filename)
        return file.exists()
    }

    fun getUriForPhoto(filename: String): Uri? {
        val file = File(pictunePath, filename)
        return Uri.parse(file.absolutePath)
    }

    private val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadId == id) {
                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val uri = downloadManager.getUriForDownloadedFile(downloadId)
                uri?.let { onCompletedListener?.invoke(uri) }
                unregisterDownloadCompleteReceiver()
            }
        }
    }

    fun cancelDownload() {
        downloadManager.remove(downloadId)
    }

    private fun registerDownloadCompleteReceiver() {
        context.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    private fun unregisterDownloadCompleteReceiver() {
        context.unregisterReceiver(onDownloadComplete)
    }

    fun startDownload(filename: String, url: String, withNotification: Boolean) {
        val subPath = "$folderName${File.separator}$filename"
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setNotificationVisibility(
                if (withNotification)
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                else {
                    registerDownloadCompleteReceiver()
                    DownloadManager.Request.VISIBILITY_VISIBLE
                }
            )
            setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, subPath)
            setTitle(filename)
        }
        downloadId = downloadManager.enqueue(request)
    }
}