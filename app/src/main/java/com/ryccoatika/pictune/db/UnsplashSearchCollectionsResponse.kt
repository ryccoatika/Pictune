package com.ryccoatika.pictune.db

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashSearchCollectionsResponse(
    @SerializedName("total")
    val total: Int? = 0,
    @SerializedName("total_pages")
    val totalPage: Int? = 0,
    @SerializedName("results")
    val results: List<UnsplashCollectionResponse?>? = listOf()
): Parcelable