package com.ryccoatika.pictune.db

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashSearchPhotosResponse(
    @SerializedName("total")
    val total: Int? = 0,
    @SerializedName("total_pages")
    val totalPages: Int? = 0,
    @SerializedName("results")
    val results: List<UnsplashPhotoResponse?>? = listOf()
): Parcelable {
//    companion object {
//        const val PARAM_
//    }
}