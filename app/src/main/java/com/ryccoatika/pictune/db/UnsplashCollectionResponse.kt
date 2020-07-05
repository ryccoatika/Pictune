package com.ryccoatika.pictune.db

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashCollectionResponse(
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("published_at")
    val publishedAt: String? = "",
    @SerializedName("total_photos")
    val totalPhotos: Int? = 0,
    @SerializedName("cover_photo")
    val coverPhoto: UnsplashPhotoResponse? = UnsplashPhotoResponse(),
    @SerializedName("user")
    val user: UnsplashUserResponse? = UnsplashUserResponse(),
    @SerializedName("links")
    val links: Link? = Link()
) : Parcelable {

    @Parcelize
    data class Link (
        @SerializedName("html")
        val html: String? = ""
    ) : Parcelable
}