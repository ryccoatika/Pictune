package com.ryccoatika.core.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.ryccoatika.core.R
import com.ryccoatika.core.domain.model.PhotoDetail
import com.ryccoatika.core.domain.model.PhotoMinimal
import java.util.*

val PhotoMinimal.getAspectRatio: Double
    get() = height.toDouble().div(width.toDouble())

val PhotoDetail.filename: String
    get() = "${user.name.toLowerCase(Locale.getDefault()).replace(" ", "-")}-${id}.jpg"

fun ImageView.setHeightAsRatio(ratio: Double) {
    layoutParams.apply {
        height = (width * ratio).toInt()
    }
}

fun ImageView.loadProfilePicture(url: String) {
    GlideApp.with(context)
        .load(url)
        .error(R.drawable.ic_user_black)
        .into(this)
        .clearOnDetach()
}

fun ImageView.loadBlurredImage(
    url: String,
    color: String
) {
    if (color != "undefined")
        background = ColorDrawable(Color.parseColor(color))
    GlideApp.with(context)
        .load(url)
        .into(this)
        .clearOnDetach()
}