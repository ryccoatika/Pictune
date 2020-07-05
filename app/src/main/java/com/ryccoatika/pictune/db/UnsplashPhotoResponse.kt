package com.ryccoatika.pictune.db

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashPhotoResponse(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("created_at")
    val craetedAt: String? = "",
    @SerializedName("width")
    val width: Int? = 0,
    @SerializedName("height")
    val height: Int? = 0,
    @SerializedName("color")
    val color: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("alt_description")
    val altDescription: String? = "",
    @SerializedName("likes")
    val likes: Int? = 0,
    @SerializedName("exif")
    val exif: Exif? = Exif(),
    @SerializedName("urls")
    val urls: Url? = Url(),
    @SerializedName("links")
    val links: Link? = Link(),
    @SerializedName("user")
    val user: UnsplashUserResponse? = UnsplashUserResponse()
) : Parcelable {

    companion object {
        const val ORDER_BY_LATEST = "latest"
        const val ORDER_BY_POPULAR = "popular"
        const val ORDER_BY_OLDEST = "oldest"
    }

    @Parcelize
    data class Url(
        @SerializedName("raw")
        val raw: String? = "",
        @SerializedName("full")
        val full: String? = "",
        @SerializedName("regular")
        val regular: String? = "",
        @SerializedName("small")
        val small: String? = "",
        @SerializedName("thumb")
        val thumb: String? = ""
    ): Parcelable


    @Parcelize
    data class Link(
        @SerializedName("html")
        val html: String? = "",
        @SerializedName("download_location")
        val downloadLocation: String? = ""
    ) : Parcelable

    @Parcelize
    data class Exif(
        @SerializedName("make")
        val make: String? = "",
        @SerializedName("model")
        val model: String? = "",
        @SerializedName("exposure_time")
        val exposureTime: String? = "",
        @SerializedName("aperture")
        val aperture: String? = "",
        @SerializedName("focal_length")
        val focalLength: String? = "",
        @SerializedName("ios")
        val iso: Int? = 0
    ) : Parcelable
}

@Parcelize
data class UnsplashPhotoStatisticsResponse(
    @SerializedName("downloads")
    val downloads: Total? = Total(),
    @SerializedName("views")
    val views: Total? = Total(),
    @SerializedName("likes")
    val likes: Total? = Total()
) : Parcelable {
    @Parcelize
    data class Total(
        @SerializedName("total")
        val total: Int? = 0
    ) : Parcelable
}