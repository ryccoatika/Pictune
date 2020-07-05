package com.ryccoatika.pictune.db

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashUserResponse(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("username")
    val username: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("portfolio_url")
    val portfolio: String? = "",
    @SerializedName("bio")
    val bio: String? = "",
    @SerializedName("location")
    val location: String? = "",
    @SerializedName("total_photos")
    val totalPhotos: Int? = 0,
    @SerializedName("total_likes")
    val totalLikes: Int? = 0,
    @SerializedName("total_collections")
    val totalCollections: Int? = 0,
    @SerializedName("instagram_username")
    val instagramUsername: String? = "",
    @SerializedName("twitter_username")
    val twitterUsername: String? = "",
    @SerializedName("links")
    val links: Links? = Links(),
    @SerializedName("profile_image")
    val profileImage: ProfileImage? = ProfileImage()
): Parcelable {
    @Parcelize
    data class Links(
        @SerializedName("html")
        val html: String? = "",
        @SerializedName("photos")
        val photos: String? = ""
    ): Parcelable

    @Parcelize
    data class ProfileImage (
        @SerializedName("small")
        val small: String? = "",
        @SerializedName("medium")
        val medium: String? = "",
        @SerializedName("large")
        val large: String? = ""
    ): Parcelable
}