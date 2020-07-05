package com.ryccoatika.pictune.download

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.unsplash_downloading_dialog.*
import okhttp3.ResponseBody
import retrofit2.Call

class DownloadDialog(context: Context)
    : Dialog(context), View.OnClickListener{

    var call: Call<ResponseBody>? = null
        set(value) {
            field = value
        }

    var indeterminate: Boolean = true
        set(value) {
            unsplash_downloading_pb?.isIndeterminate = value
            field = value
        }

    var title: String = context.getString(R.string.text_downloading)
        set(value) {
            unsplash_downloading_tv?.text = value
            field = value
        }

    var progress: Int = 0
        set(value) {
            unsplash_downloading_pb?.progress = value
            unsplash_downloading_tv_progress?.text = "$value%"
            field = value
        }

    var cancelEnable: Boolean = false
        set(value) {
            unsplash_downloading_btn_cancel?.isEnabled = value
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.unsplash_downloading_dialog)
        setCanceledOnTouchOutside(false)
        setCancelable(true)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        unsplash_downloading_btn_cancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.unsplash_downloading_btn_cancel -> {
                call?.cancel()
                dismiss()
            }
        }
    }
}