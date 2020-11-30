package com.ryccoatika.pictune.download

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.ryccoatika.core.domain.model.Urls
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.dialog_quality_chooser.*

class DownloadQualityChooserDialog(
    context: Context,
    private val url: Urls
) : Dialog(context), View.OnClickListener {

    private var onResultListener: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_quality_chooser)
        setCancelable(true)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        download_small.setOnClickListener(this)
        download_medium.setOnClickListener(this)
        download_large.setOnClickListener(this)
        download_original.setOnClickListener(this)
    }

    fun setOnResultListener(listener: (url: String) -> Unit): DownloadQualityChooserDialog {
        this.onResultListener = listener
        return this
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.download_small -> {
                onResultListener?.invoke(url.small)
                dismiss()
            }
            R.id.download_medium -> {
                onResultListener?.invoke(url.regular)
                dismiss()
            }
            R.id.download_large -> {
                onResultListener?.invoke(url.full)
                dismiss()
            }
            R.id.download_original -> {
                onResultListener?.invoke(url.raw)
                dismiss()
            }
        }
    }
}