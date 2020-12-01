package com.ryccoatika.pictune.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.widget.Toast
import com.ryccoatika.core.domain.model.PhotoDetail
import java.io.File
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun PhotoDetail.getVisibleCropHint(): Rect? {

    val screenWidth: Double
    val screenHeight: Double
    Resources.getSystem().displayMetrics.let {
        screenWidth = min(it.widthPixels, it.heightPixels).toDouble()
        screenHeight = max(it.widthPixels, it.heightPixels).toDouble()
    }
    if (screenHeight > 0 && screenWidth > 0 && width > 0 && height > 0) {
        val screenRatio = screenWidth / screenHeight
        val photoRatio = width.toDouble() / height.toDouble()
        val resizeFactor = if (screenRatio >= photoRatio)
            width / screenWidth
        else
            height / screenHeight

        val newWidth = screenWidth * resizeFactor
        val newHeight = screenHeight * resizeFactor
        val left = (width - newWidth) / 2
        val top = (height - newHeight) / 2
        val right = newWidth + left
        val bottom = newHeight + top
        return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }
    return null
}

fun File.dirSize(): Long {
    if (this.exists()) {
        var results: Long = 0
        listFiles()?.forEach {
            results += if (it.isDirectory)
                this.dirSize()
            else
                it.length()
        }
        return results / 1024 / 1024
    }
    return 0
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun formattedDate(date: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
    val df = DateFormat.getDateInstance()
    return df.format(sdf.parse(date) as Date)
}

fun formattedNumber(number: Int): String {
    val df = NumberFormat.getNumberInstance(Locale.getDefault())
    return df.format(number)
}