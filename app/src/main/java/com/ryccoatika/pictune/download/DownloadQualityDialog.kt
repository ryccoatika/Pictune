package com.ryccoatika.pictune.download

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.db.UnsplashPhotoResponse
import kotlinx.android.synthetic.main.unsplash_download_quality_choose_dialog.*
import java.io.File

class DownloadQualityDialog(
    context: Context,
    private val filename: String,
    private val url: UnsplashPhotoResponse.Url
): Dialog(context), View.OnClickListener {

    private lateinit var downloadManager: DownloadManager
    private lateinit var foldername: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.unsplash_download_quality_choose_dialog)
        setCancelable(true)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        foldername = context.getString(R.string.app_name)

        download_small.setOnClickListener(this)
        download_medium.setOnClickListener(this)
        download_large.setOnClickListener(this)
        download_original.setOnClickListener(this)
    }

    private fun startDownload(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(filename)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_PICTURES,
            foldername + File.separator + filename)
        downloadManager.enqueue(request)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.download_small -> {
                url.small?.let { startDownload(it) }
                dismiss()
            }
            R.id.download_medium -> {
                url.regular?.let { startDownload(it) }
                dismiss()
            }
            R.id.download_large -> {
                url.full?.let { startDownload(it) }
                dismiss()
            }
            R.id.download_original -> {
                url.raw?.let { startDownload(it) }
                dismiss()
            }
        }
    }
}